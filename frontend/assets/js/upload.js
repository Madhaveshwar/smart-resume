// ── LOAD JOB TITLES (FINAL FIX) ─────────────────────────
async function loadJobTitles() {
  console.log("🔥 Loading job titles...");

  const sel = document.getElementById("jobTitleSelect");

  if (!sel) {
    console.error("❌ Dropdown not found");
    return;
  }

  try {
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

    // ✅ fallback so UI NEVER empty
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