// ── OFFLINE ATS ENGINE ─────────────────────────
console.log("✅ Initialization started. Cloud & Claude APIs removed. Running 100% LOCAL MATCHING ENGINE.");

// Basic Dictionary for algorithmic extraction
const TECH_DICTIONARY = [
  "java", "python", "react", "node", "sql", "aws", "docker", "kubernetes", "javascript", "typescript",
  "c++", "c#", "spring", "django", "html", "css", "mongodb", "postgresql", "rest", "api", "microservices",
  "agile", "scrum", "git", "ci/cd", "linux", "cloud", "azure", "gcp", "angular", "vue", "redis", "kafka"
];

// Pre-defined mandatory clusters based on typical titles
const roleSkillMaps = {
  "Software Engineer": ["java", "python", "git", "sql", "api", "agile", "c++"],
  "Backend Developer": ["java", "spring", "node", "sql", "api", "microservices", "docker"],
  "Frontend Developer": ["html", "css", "javascript", "react", "typescript", "api"],
  "Full Stack Developer": ["react", "node", "javascript", "mongodb", "sql", "api", "aws"],
  "Data Scientist": ["python", "sql", "data", "pandas", "machine learning"],
  "DevOps Engineer": ["aws", "docker", "kubernetes", "ci/cd", "linux", "cloud"]
};

async function initUpload() {
  const fileInput = document.getElementById("fileInput");
  const analyzeBtn = document.getElementById("analyzeBtn");
  const dropzone = document.getElementById("dropzone");
  const filePillsContainer = document.getElementById("filePillsContainer");

  let selectedFiles = [];

  function updatePills() {
    filePillsContainer.innerHTML = selectedFiles.map((f, i) => `
      <div class="bg-blue-900 border border-blue-700 text-blue-100 rounded-full px-3 py-1.5 flex items-center gap-2 text-xs font-semibold shadow-sm">
        <span class="truncate max-w-[150px]">\${f.name}</span>
        <button type="button" class="text-blue-300 hover:text-white transition-colors" onclick="removeFile(\${i})">
          <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
          </svg>
        </button>
      </div>
    `).join('');
  }

  window.removeFile = (index) => {
    selectedFiles.splice(index, 1);
    updatePills();
  };

  if (fileInput && dropzone && filePillsContainer) {
    fileInput.addEventListener("change", () => {
      if (fileInput.files.length > 0) {
        selectedFiles = [...selectedFiles, ...Array.from(fileInput.files)];
        fileInput.value = "";
        updatePills();
      }
    });

    dropzone.addEventListener("dragover", (e) => {
      e.preventDefault();
      dropzone.classList.add("border-blue-500", "bg-gray-800");
    });

    dropzone.addEventListener("dragleave", (e) => {
      e.preventDefault();
      dropzone.classList.remove("border-blue-500", "bg-gray-800");
    });

    dropzone.addEventListener("drop", (e) => {
      e.preventDefault();
      dropzone.classList.remove("border-blue-500", "bg-gray-800");
      if (e.dataTransfer && e.dataTransfer.files && e.dataTransfer.files.length > 0) {
        selectedFiles = [...selectedFiles, ...Array.from(e.dataTransfer.files)];
        updatePills();
      }
    });
  }

  if (analyzeBtn && fileInput) {
    analyzeBtn.addEventListener("click", async (e) => {
      e.preventDefault();

      if (selectedFiles.length === 0) {
        alert("Please select at least one file.");
        return;
      }

      const jobTitle = document.getElementById("jobTitleSelect").value;
      if (!jobTitle) {
        alert("Please select a Job Title.");
        return;
      }

      const jobDesc = document.getElementById("jobDescInput").value;

      analyzeBtn.disabled = true;

      try {
        let resumesParsed = [];

        for (let i = 0; i < selectedFiles.length; i++) {
          analyzeBtn.innerHTML = `Scanning \${selectedFiles[i].name}... ⏳`;
          const rawText = await window.Extractor.extractTextFromFile(selectedFiles[i]);

          // ── OFFLINE DYNAMIC SCORING ALGORITHM ──

          // 1. Context setup
          let requiredSkills = roleSkillMaps[jobTitle] || TECH_DICTIONARY.slice(0, 5);
          if (jobDesc) {
            const descWords = jobDesc.toLowerCase().replace(/[^a-z0-9\s]/g, '').split(/\s+/);
            const foundInDesc = TECH_DICTIONARY.filter(tech => descWords.includes(tech));
            if (foundInDesc.length > 0) requiredSkills = [...new Set([...requiredSkills, ...foundInDesc])];
          }

          const cleanText = rawText.toLowerCase().replace(/[^a-z0-9#\+\-\s]/g, ' ');

          // 2. SKILLS MATCH (40%)
          const matchedSkills = requiredSkills.filter(skill => cleanText.includes(skill));
          const missingSkills = requiredSkills.filter(skill => !cleanText.includes(skill));
          const skillsScoreRaw = requiredSkills.length > 0 ? (matchedSkills.length / requiredSkills.length) * 40 : 40;
          const skillsMatchPct = Math.round((skillsScoreRaw / 40) * 100);

          // 3. KEYWORDS MATCH (30%)
          let keywordScoreRaw = 0;
          const titleWords = jobTitle.toLowerCase().split(/\s+/).filter(w => w.length > 3);
          const hasTitleWords = titleWords.filter(w => cleanText.includes(w)).length;
          keywordScoreRaw += (hasTitleWords > 0) ? (hasTitleWords / titleWords.length) * 15 : 0;
          // Sub-keywords
          const extraKeyHits = TECH_DICTIONARY.filter(tech => !requiredSkills.includes(tech) && cleanText.includes(tech));
          keywordScoreRaw += Math.min(extraKeyHits.length * 3, 15);
          const keywordMatchPct = Math.round((keywordScoreRaw / 30) * 100);

          // 4. EXPERIENCE / PROJECTS MATCH (20%)
          let expScoreRaw = 0;
          const expRegex = /\b([1-9]|1[0-9])\+?\s*(years?|yrs?)\b/i;
          const expMatchObj = rawText.match(expRegex);
          let experienceStr = "No explicit experience timeline detected";
          if (expMatchObj) {
            const yrs = parseInt(expMatchObj[1]);
            expScoreRaw += Math.min(yrs * 4, 10);
            experienceStr = `\${yrs}+ Years`;
          }
          if (cleanText.includes("project") || cleanText.includes("github") || cleanText.includes("portfolio")) {
            expScoreRaw += 10;
          }
          const experienceMatchPct = Math.round((expScoreRaw / 20) * 100);

          // 5. RESUME QUALITY / FORMAT (10%)
          let qualScoreRaw = 0;
          const sections = ["experience", "education", "skills", "summary", "projects"];
          const foundSections = sections.filter(sec => cleanText.includes(sec));
          qualScoreRaw += Math.min(foundSections.length * 2, 10);

          const totalScore = Math.ceil(skillsScoreRaw + keywordScoreRaw + expScoreRaw + qualScoreRaw);

          // 6. Actionable Insights Generation
          let strengths = [];
          let weaknesses = [];
          let impSkills = [];
          let impProjects = [];
          let impKeys = [];
          let impTips = [];

          if (matchedSkills.length > 0) strengths.push(`Strong core knowledge in \${matchedSkills.slice(0,3).join(', ')}`);
          if (expMatchObj) strengths.push(`Clear experience metrics tracking (\${experienceStr})`);
          if (foundSections.includes('projects')) strengths.push(`Practical project application visible`);

          if (missingSkills.length > 0) {
            weaknesses.push(`Missing core job requirements: \${missingSkills.slice(0,2).join(', ')}`);
            impSkills.push(`Learn and master \${missingSkills[0]}`);
            if (missingSkills.length > 1) impSkills.push(`Gain familiarity with \${missingSkills[1]}`);
            impProjects.push(`Build a full-stack project integrating \${missingSkills[0]}`);
          } else {
            impSkills.push("Keep current skills updated with latest industry versions");
          }

          if (extraKeyHits.length < 2) {
            weaknesses.push(`Low ATS secondary keyword density`);
            impKeys = ["REST APIs", "Microservices", "Cloud Deployment", "Agile"];
          } else {
            impKeys = missingSkills.slice(0, 4);
            if (impKeys.length === 0) impKeys.push("Leadership", "Mentoring", "System Design");
          }

          if (!foundSections.includes('summary')) impTips.push("Add a strong professional summary at the very top");
          if (!expMatchObj) impTips.push("Quantify your achievements (e.g., 'Improved performance by 30%')");
          impTips.push("Ensure bullet points follow the XYZ or STAR methodology to beat ATS parsers");

          if (strengths.length === 0) strengths.push("Basic document formatting is readable to ATS");
          if (weaknesses.length === 0) weaknesses.push("Consider expanding on leadership or architectural decisions");
          if (impProjects.length === 0) impProjects.push("Deploy a large-scale personal portfolio site");

          const whyScore = [
            `Candidate matches ${matchedSkills.length} out of ${requiredSkills.length} required skills.`,
            missingSkills.length > 0
              ? `Missing critical technologies: ${missingSkills.slice(0, 3).join(', ')}.`
              : "No critical technology gaps detected for this role.",
            expScoreRaw < 10
              ? "Experience depth is currently low for this role."
              : "Experience depth is acceptable for this role."
          ];

          const profileSummary = `Candidate shows strong foundation in ${matchedSkills.slice(0, 3).join(', ') || 'core technologies'} but lacks ${missingSkills.slice(0, 3).join(', ') || 'major role-specific gaps'}.`;

          const hiringDecision = totalScore > 80
            ? "Strong fit for direct interview"
            : totalScore >= 60
              ? "Can be considered for internship or junior track"
              : "Not suitable for immediate hiring";

          const hiringImpact = totalScore > 80
            ? "Good fit for junior role with low onboarding risk."
            : totalScore >= 60
              ? "Can be considered after targeted skill improvement."
              : "Not suitable for immediate hiring; recommend structured upskilling.";

          const recruiterNote = `Candidate requires improvement in ${missingSkills.length} key areas before being considered for this role.`;
          const oneLineSummary = totalScore > 80
            ? "Highly recommended candidate."
            : totalScore >= 60
              ? "Potential candidate with improvements needed."
              : "Not suitable for current role.";

          const improvementPlan = missingSkills.slice(0, 5).map(skill =>
            `${skill} -> Required for frontend/backend job delivery -> Build 2 projects using ${skill}`
          );

          const now = new Date().toISOString();

          resumesParsed.push({
            name: `Candidate \${i+1}`,
            fileName: selectedFiles[i].name,
            email: "N/A",
            phone: "N/A",
            experience: experienceStr,
            score: totalScore,
            matchPercentage: totalScore,
            skillsMatch: Math.min(skillsMatchPct, 100),
            experienceMatch: Math.min(experienceMatchPct, 100),
            keywordMatch: Math.min(keywordMatchPct, 100),
            matchedSkills: matchedSkills,
            missingSkills: missingSkills,
            whyScore: whyScore,
            profileSummary: profileSummary,
            hiringDecision: hiringDecision,
            hiringImpact: hiringImpact,
            recruiterNote: recruiterNote,
            oneLineSummary: oneLineSummary,
            improvementPlan: improvementPlan,
            strengths: strengths,
            weaknesses: weaknesses,
            improvements: {
              skills: impSkills,
              projects: impProjects,
              keywords: impKeys,
              resumeTips: impTips
            },
            timestamp: now,
            jobTitle: jobTitle
          });
        }

        analyzeBtn.innerHTML = "Finalizing Match Results... 📊";

        // Save aggressively to localStorage
        try {
          let existing = JSON.parse(localStorage.getItem("resumes")) || [];
          if (!Array.isArray(existing)) {
            existing = [];
            localStorage.setItem("resumes", JSON.stringify([]));
          }
          resumesParsed.forEach(newResume => {
            existing.push(newResume);
          });
          localStorage.setItem("resumes", JSON.stringify(existing));
          console.log("Saved resumes:", existing);
        } catch (err) {
          console.warn("Could not save to localStorage", err);
        }

        console.log("✅ Offline Analysis complete!", resumesParsed);

        window.currentAnalyzeData = { resumes: resumesParsed, jobTitle };
        showResults(resumesParsed);

      } catch (e) {
        console.error("❌ Upload error:", e);
        alert("Upload failed: " + e.message);
      } finally {
        analyzeBtn.disabled = false;
        analyzeBtn.innerHTML = "🔍 Analyse Resumes";
      }
    });
  } else {
    console.error("❌ Missing attach items. analyzeBtn:", !!analyzeBtn, "fileInput:", !!fileInput);
  }
}

if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', initUpload);
} else {
  initUpload();
}

function showResults(resumesArray) {
  document.getElementById("heroSection")?.classList.add("hidden");
  document.getElementById("uploadSection")?.classList.add("hidden");
  document.getElementById("resultsHeader")?.classList.remove("hidden");

  if (!window.filterBound) {
    document.getElementById("filterAll")?.addEventListener('click', () => applyFilter('all'));
    document.getElementById("filterHigh")?.addEventListener('click', () => applyFilter('high'));
    document.getElementById("filterMed")?.addEventListener('click', () => applyFilter('med'));
    document.getElementById("filterLow")?.addEventListener('click', () => applyFilter('low'));
    window.filterBound = true;
  }

  const jobTitle = window.currentAnalyzeData?.jobTitle || "";
  document.getElementById("resultJobTitle").textContent = jobTitle ? "- " + jobTitle : "";
  document.getElementById("resultSubtitle").textContent = resumesArray.length + " resume(s) matched filter.";

  const container = document.getElementById("resumeCards");
  
  if (typeof renderResumeCards === "function") {
    container.innerHTML = renderResumeCards(resumesArray || [], 'upload');
  } else {
    container.innerHTML = "<p>Error: components.js missing rendering function.</p>";
  }
}

function applyFilter(tier) {
   document.querySelectorAll('.filter-btn').forEach(btn => btn.classList.remove('bg-blue-600', 'text-white'));
   document.querySelectorAll('.filter-btn').forEach(btn => btn.classList.add('bg-gray-800', 'text-gray-400'));
   
   let btnMap = { 'all':'filterAll', 'high':'filterHigh', 'med':'filterMed', 'low':'filterLow' };
   const targetBtn = document.getElementById(btnMap[tier]);
   if(targetBtn) {
       targetBtn.classList.add('bg-blue-600', 'text-white');
       targetBtn.classList.remove('bg-gray-800', 'text-gray-400');
   }

   const allResumes = window.currentAnalyzeData?.resumes || [];
   let filtered = [];
   if(tier === 'high') {
       filtered = allResumes.filter(r => parseInt(r.score) >= 80);
   } else if (tier === 'med') {
       filtered = allResumes.filter(r => parseInt(r.score) >= 60 && parseInt(r.score) < 80);
   } else if (tier === 'low') {
       filtered = allResumes.filter(r => parseInt(r.score) < 60);
   } else {
       filtered = allResumes;
   }
   
   showResults(filtered);
}