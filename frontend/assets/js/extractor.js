// ── extractor.js ────────────────────────────────────────────────────────
// Local text extraction engine leveraging browser CDNs (pdf.js, mammoth)

/**
 * Extracts raw text from a given File object.
 * Identifies file type and delegates to the appropriate reader.
 */
async function extractTextFromFile(file) {
  const ext = file.name.split('.').pop().toLowerCase();
  
  try {
    if (ext === 'pdf') {
      return await extractPDFText(file);
    } else if (ext === 'docx') {
      return await extractDOCXText(file);
    } else if (ext === 'txt') {
      return await file.text();
    } else {
      console.warn(`Unsupported extension: ${ext}, attempting raw text read...`);
      return await file.text();
    }
  } catch (err) {
    console.error(`Error extracting text from ${file.name}:`, err);
    throw new Error(`Failed to read ${file.name}`);
  }
}

/**
 * Extract PDF text using pdfjs-dist
 */
async function extractPDFText(file) {
  if (!window.pdfjsLib) throw new Error("pdf.js is not loaded");
  
  const arrayBuffer = await file.arrayBuffer();
  const pdf = await window.pdfjsLib.getDocument({ data: arrayBuffer }).promise;
  
  let fullText = "";
  for (let i = 1; i <= pdf.numPages; i++) {
    const page = await pdf.getPage(i);
    const content = await page.getTextContent();
    const strings = content.items.map(item => item.str);
    fullText += strings.join(" ") + "\n";
  }
  
  return fullText.trim();
}

/**
 * Extract DOCX text using Mammoth.js
 */
async function extractDOCXText(file) {
  if (!window.mammoth) throw new Error("mammoth.js is not loaded");
  
  const arrayBuffer = await file.arrayBuffer();
  const result = await mammoth.extractRawText({ arrayBuffer });
  return result.value.trim();
}

window.Extractor = {
  extractTextFromFile
};
