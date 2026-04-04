// ── Dashboard Page Logic ──────────────────────────────────────────

document.addEventListener("DOMContentLoaded", () => {
  if (typeof setActiveNav === 'function') setActiveNav('dashboard');
  loadDashboard();

  document.getElementById("clearBtn").addEventListener("click", () => {
    if (!confirm("Delete ALL resumes from your local history?")) return;
    localStorage.removeItem('resumes');
    loadDashboard();
  });
});

function loadDashboard() {
  const cards    = document.getElementById("resumeCards");
  const subtitle = document.getElementById("dashSubtitle");

  cards.innerHTML = `<p class="text-gray-500 text-center py-10">Loading local history…</p>`;

  try {
    const raw = localStorage.getItem('resumes');
    let data = [];
    if(raw) {
       const parsed = JSON.parse(raw);
       data = Array.isArray(parsed) ? parsed : (parsed.resumes || []);
    }

    console.log("Saved resumes:", data);

    subtitle.textContent = `\${data.length} resume(s) locally stored in browser`;
    
    if (typeof renderResumeCards === "function") {
      let summaryHtml = "";
      if (data.length > 0) {
         const avgScore = Math.round(data.reduce((acc, r) => acc + (parseInt(r.score)||0), 0) / data.length);
         const topScore = Math.max(...data.map(r => parseInt(r.score)||0));
         summaryHtml = `
            <div class="grid grid-cols-3 gap-4 mb-8 bg-gray-900 border border-gray-800 p-6 rounded-3xl shadow-2xl relative overflow-hidden">
               <div class="absolute -top-10 -right-10 w-40 h-40 opacity-5 blur-3xl pointer-events-none rounded-full bg-blue-500"></div>
               <div class="text-center z-10"><p class="text-gray-400 text-[0.65rem] font-bold uppercase tracking-widest mb-1 mt-2">Total Analyzed</p><p class="text-3xl font-black text-white">\${data.length}</p></div>
               <div class="text-center border-l border-r border-gray-800/80 z-10"><p class="text-gray-400 text-[0.65rem] font-bold uppercase tracking-widest mb-1 mt-2">Avg Match</p><p class="text-3xl font-black text-blue-400">\${avgScore}%</p></div>
               <div class="text-center z-10"><p class="text-gray-400 text-[0.65rem] font-bold uppercase tracking-widest mb-1 mt-2">Top Score</p><p class="text-3xl font-black text-yellow-500">\${topScore}%</p></div>
            </div>
         `;
      }
      
      cards.innerHTML = summaryHtml + renderResumeCards(data, "dashboard");
    } else {
      cards.innerHTML = `<p class="text-red-400 text-center">Missing visualization dependencies.</p>`;
    }
  } catch (e) {
    cards.innerHTML = `<p class="text-red-400 text-center py-10">Error loading local history data from browser.</p>`;
    subtitle.textContent = "";
    console.error("Local storage read error", e);
  }
}
