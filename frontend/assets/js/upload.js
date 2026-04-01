// 🔥 DEBUG
console.log("🔥 upload.js loaded");

// ── LOAD JOB TITLES ─────────────────────────
async function loadJobTitles() {
  console.log("🔥 Loading job titles...");

  const sel = document.getElementById("jobTitleSelect");

  if (!sel) {
    console.error("❌ Dropdown not found");
    return;
  }

  try {
    console.log("🌐 API:", window.API_BASE);

    const res = await fetch(window.API_BASE + "/api/job-titles");

    if (!res.ok) throw new Error("API failed");

    const titles = await res.json();

    console.log("✅ Titles:", titles);

    sel.innerHTML = `<option value="">-- Select or leave blank --</option>`;

    titles.forEach(t => {
      const opt = document.createElement("option");
      opt.value = t;
      opt.textContent = t;
      sel.appendChild(opt);
    });

  } catch (e) {
    console.error("❌ API FAILED → using fallback", e);

    sel.innerHTML = `
      <option value="">-- Select or leave blank --</option>
      <option>Software Engineer</option>
      <option>Data Scientist</option>
      <option>Web Developer</option>
      <option>AI Engineer</option>
      <option>Backend Developer</option>
      <option>Frontend Developer</option>
    `;
  }
}

// ── MAIN LOAD ─────────────────────────
document.addEventListener("DOMContentLoaded", async () => {
  console.log("✅ DOM loaded");

  // 🔥 FIX: Load job titles
  await loadJobTitles();

  // ── FILE UPLOAD ─────────────────────
  const fileInput = document.getElementById("fileInput");
  const submitBtn = document.getElementById("submitBtn");

  if (submitBtn && fileInput) {
    submitBtn.addEventListener("click", async () => {

      if (fileInput.files.length === 0) {
        alert("Please select a file.");
        return;
      }

      submitBtn.disabled = true;
      submitBtn.innerHTML = "Analysing...";

      const formData = new FormData();
      formData.append("file", fileInput.files[0]);

      formData.append(
        "jobTitle",
        document.getElementById("jobTitleSelect")?.value || ""
      );

      formData.append(
        "jobDescription",
        document.getElementById("jobDescInput")?.value || ""
      );

      try {
        const res = await fetch(window.API_BASE + "/api/upload", {
          method: "POST",
          body: formData
        });

        if (!res.ok) throw new Error("Upload failed");

        const data = await res.json();
        console.log("✅ Response:", data);

        showResults(data);

      } catch (e) {
        console.error("❌ Upload error:", e);
        alert("Error uploading file.");
      } finally {
        submitBtn.disabled = false;
        submitBtn.innerHTML = "🔍 Analyse Resumes";
      }
    });
  }
});


// ── RESULTS UI ─────────────────────────
function showResults(data) {
  document.getElementById("heroSection")?.classList.add("hidden");
  document.getElementById("uploadSection")?.classList.add("hidden");
  document.getElementById("resultsHeader")?.classList.remove("hidden");

  document.getElementById("resultJobTitle").textContent =
    data.jobTitle ? `— ${data.jobTitle}` : "";

  document.getElementById("resultSubtitle").textContent =
    `${data.totalResumes} resume(s) processed`;

  const container = document.getElementById("resumeCards");

  container.innerHTML = data.resumes.map((r, i) => `
    <div style="background:#111827; padding:20px; margin:15px 0; border-radius:10px;">
      
      <h3 style="color:white;">${i + 1}. ${r.name}</h3>

      <p style="color:#22c55e;">Match Score: ${r.score}%</p>

      <p style="color:#4ade80;">✔ ${r.matchedSkills.join(", ")}</p>

      <p style="color:#f87171;">❌ ${r.missingSkills.join(", ")}</p>

      <ul style="color:white;">
        ${r.suggestions.map(s => `<li>${s}</li>`).join("")}
      </ul>

    </div>
  `).join("");
}