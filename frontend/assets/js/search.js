// ── Search Page Logic ─────────────────────────────────────────────

document.addEventListener("DOMContentLoaded", () => {
  setActiveNav('search');

  const searchBtn   = document.getElementById("searchBtn");
  const searchInput = document.getElementById("searchInput");

  // Run search on button click
  searchBtn.addEventListener("click", () => runSearch());

  // Run search on Enter key
  searchInput.addEventListener("keydown", e => {
    if (e.key === "Enter") runSearch();
  });

  // If ?keyword= is in the URL, pre-fill and run automatically
  const params  = new URLSearchParams(window.location.search);
  const keyword = params.get("keyword");
  if (keyword) {
    searchInput.value = keyword;
    runSearch();
  }
});

async function runSearch() {
  const keyword  = document.getElementById("searchInput").value.trim();
  const normalizedKeyword = keyword.toLowerCase();
  const subtitle = document.getElementById("searchSubtitle");
  const cards    = document.getElementById("resumeCards");

  cards.innerHTML = `<p class="text-gray-500 text-center py-10">Searching…</p>`;
  subtitle.textContent = "";

  try {
    const url = keyword
      ? `${API_BASE}/api/search?keyword=${encodeURIComponent(keyword)}`
      : `${API_BASE}/api/search`;
    const res     = await fetch(url);
    const resumes = await res.json();

    const filteredResumes = normalizedKeyword
      ? resumes.filter(r => {
          const haystack = [
            r.name,
            r.fileName,
            r.skills,
            r.matchedSkills,
            r.missingSkills,
            r.profileSummary,
            r.hiringDecision,
            r.hiringImpact
          ].join(' ').toLowerCase();
          return haystack.includes(normalizedKeyword);
        })
      : resumes;

    if (keyword) {
      subtitle.textContent = `Showing results for: "${keyword}" (${filteredResumes.length} found)`;
    } else {
      subtitle.textContent = `Showing all resumes (${filteredResumes.length})`;
    }
    cards.innerHTML = renderResumeCards(filteredResumes);
  } catch (e) {
    cards.innerHTML = `<p class="text-red-400 text-center py-10">Error connecting to backend. Make sure the Spring Boot server is running.</p>`;
    console.error(e);
  }
}
