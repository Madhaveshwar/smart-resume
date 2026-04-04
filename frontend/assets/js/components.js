// Shared UI components used across all pages

function asList(value) {
  if (!value) return [];
  if (Array.isArray(value)) return value.filter(Boolean);
  if (typeof value === 'string') {
    const splitter = value.includes('||') ? '||' : ',';
    return value.split(splitter).map(s => s.trim()).filter(Boolean);
  }
  return [];
}

function scoreColor(score) {
  if (score > 80) return { text: 'text-green-400', chip: 'bg-green-950/50 border-green-800 text-green-300', bar: 'bg-green-500' };
  if (score >= 60) return { text: 'text-yellow-400', chip: 'bg-yellow-950/50 border-yellow-800 text-yellow-300', bar: 'bg-yellow-500' };
  return { text: 'text-red-400', chip: 'bg-red-950/50 border-red-800 text-red-300', bar: 'bg-red-500' };
}

function deriveWhyScore(r) {
  if (Array.isArray(r.whyScore)) return r.whyScore;
  if (typeof r.whyScore === 'string' && r.whyScore.trim()) return asList(r.whyScore);

  const matched = asList(r.matchedSkills);
  const missing = asList(r.missingSkills);
  const requiredCount = matched.length + missing.length;
  const reasons = [];

  if (requiredCount > 0) {
    reasons.push(`Candidate matches ${matched.length} out of ${requiredCount} required skills.`);
  }
  if (missing.length > 0) {
    reasons.push(`Missing critical skills: ${missing.slice(0, 3).join(', ')}.`);
  }
  reasons.push((r.experienceMatch || 0) < 60
    ? 'Experience alignment is currently low for this role.'
    : 'Experience alignment is acceptable for this role.');
  return reasons;
}

function deriveProfileSummary(r) {
  if (r.profileSummary) return r.profileSummary;
  const matched = asList(r.matchedSkills);
  const missing = asList(r.missingSkills);
  const strong = matched.length ? matched.slice(0, 3).join(', ') : 'general foundational skills';
  const gap = missing.length ? missing.slice(0, 3).join(', ') : 'no major role-specific gaps';
  return `Candidate shows strength in ${strong} but has gaps in ${gap}.`;
}

function deriveImpact(r, score) {
  if (r.hiringImpact) return r.hiringImpact;
  if (score > 80) return 'Good fit for immediate interview and shortlisting.';
  if (score >= 60) return 'Can be considered for junior role after focused improvements.';
  return 'Not suitable for immediate hiring; consider internship track after upskilling.';
}

function deriveDecision(r, score) {
  if (r.hiringDecision) return r.hiringDecision;
  if (score > 80) return 'Strong Candidate';
  if (score > 60) return 'Moderate Fit';
  return 'Needs Improvement';
}

function derivePlan(r) {
  if (Array.isArray(r.improvementPlan)) return r.improvementPlan;
  if (typeof r.improvementPlan === 'string' && r.improvementPlan.trim()) return asList(r.improvementPlan);

  if (r.suggestions) return asList(r.suggestions);

  const missing = asList(r.missingSkills);
  return missing.slice(0, 4).map(skill => `${skill} -> Required for this role -> Build 2 projects using ${skill}`);
}

function deriveRecruiterNote(r) {
  if (r.recruiterNote) return r.recruiterNote;
  const missing = asList(r.missingSkills);
  return `Candidate requires improvement in ${missing.length} key areas before being considered for this role.`;
}

function deriveOneLineSummary(r, score) {
  if (r.oneLineSummary) return r.oneLineSummary;
  if (score > 80) return 'Highly recommended candidate.';
  if (score > 60) return 'Potential candidate with improvements needed.';
  return 'Not suitable for current role.';
}

function renderResumeCards(resumesArray) {
  if (!resumesArray || resumesArray.length === 0) {
    return `<div class="text-center mt-10"><p class="text-gray-500">No resumes found matching this criteria.</p></div>`;
  }

  const resumes = [...resumesArray]
    .map(r => ({ ...r, finalScore: parseInt(r.matchPercentage ?? r.score ?? 0, 10) || 0 }))
    .sort((a, b) => b.finalScore - a.finalScore);

  return resumes.map((r, idx) => {
    const color = scoreColor(r.finalScore);
    const matchedSkills = asList(r.matchedSkills);
    const missingSkills = asList(r.missingSkills);
    const whyScore = deriveWhyScore(r);
    const improvement = derivePlan(r);
    const decision = deriveDecision(r, r.finalScore);
    const decisionBadge = r.finalScore > 80
      ? 'background:#16a34a;color:white;padding:4px 10px;border-radius:6px;'
      : (r.finalScore > 60
        ? 'background:#f59e0b;color:white;padding:4px 10px;border-radius:6px;'
        : 'background:#dc2626;color:white;padding:4px 10px;border-radius:6px;');

    return `
      <article class="bg-gray-900 border border-gray-800 rounded-3xl p-6 relative overflow-hidden ${idx === 0 ? 'ring-1 ring-yellow-500/40' : ''}">
        ${idx === 0 ? '<div class="absolute top-0 right-0 px-4 py-1.5 bg-gradient-to-r from-yellow-400 to-amber-500 text-gray-900 text-xs font-black rounded-bl-2xl">Top Candidate</div>' : ''}

        <div class="flex flex-col md:flex-row md:items-center justify-between gap-5 border-b border-gray-800 pb-5 mb-5">
          <div>
            <p class="text-xs font-bold tracking-widest text-gray-400">Rank #${idx + 1}</p>
            <h3 class="text-2xl font-black mt-1">${r.name || 'Candidate'}</h3>
            <p class="text-xs text-gray-500 mt-1">${r.fileName || 'Unknown file'}</p>
          </div>
          <div class="text-right">
            <p class="text-xs text-gray-500 font-bold">MATCH %</p>
            <p class="text-5xl font-black ${color.text}">${r.finalScore}<span class="text-xl text-gray-500">%</span></p>
            <span style="${decisionBadge}">${decision}</span>
          </div>
        </div>

        <section class="bg-gray-950 border border-gray-800 rounded-2xl p-4 mb-5">
          <p class="text-[0.7rem] uppercase tracking-widest text-blue-400 font-bold">Score Breakdown</p>
          <div class="grid grid-cols-1 md:grid-cols-3 gap-3 mt-3 text-xs">
            <div>
              <div class="flex justify-between text-gray-400 mb-1"><span>Skills Match</span><span>${r.skillsMatch || 0}%</span></div>
              <div class="h-2 rounded-full bg-gray-800 overflow-hidden"><div class="h-full bg-blue-500" style="width:${r.skillsMatch || 0}%"></div></div>
            </div>
            <div>
              <div class="flex justify-between text-gray-400 mb-1"><span>Keyword Match</span><span>${r.keywordMatch || 0}%</span></div>
              <div class="h-2 rounded-full bg-gray-800 overflow-hidden"><div class="h-full bg-cyan-500" style="width:${r.keywordMatch || 0}%"></div></div>
            </div>
            <div>
              <div class="flex justify-between text-gray-400 mb-1"><span>Experience Match</span><span>${r.experienceMatch || 0}%</span></div>
              <div class="h-2 rounded-full bg-gray-800 overflow-hidden"><div class="h-full bg-purple-500" style="width:${r.experienceMatch || 0}%"></div></div>
            </div>
          </div>
        </section>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <section class="bg-gray-950 border border-gray-800 rounded-2xl p-4">
            <p class="text-[0.7rem] uppercase tracking-widest text-sky-400 font-bold">Why This Score?</p>
            <ul class="mt-2 space-y-1 text-sm text-gray-300">${whyScore.map(w => `<li>${w}</li>`).join('')}</ul>
          </section>

          <section class="bg-gray-950 border border-gray-800 rounded-2xl p-4">
            <p class="text-[0.7rem] uppercase tracking-widest text-emerald-400 font-bold">Profile Summary</p>
            <p class="mt-2 text-sm text-gray-300">${deriveProfileSummary(r)}</p>
            <p class="mt-3 text-xs text-cyan-300"><b>Quick Decision:</b> ${deriveOneLineSummary(r, r.finalScore)}</p>
          </section>

          <section class="bg-gray-950 border border-gray-800 rounded-2xl p-4">
            <p class="text-[0.7rem] uppercase tracking-widest text-green-400 font-bold">Strengths</p>
            <div class="mt-2 flex flex-wrap gap-1.5">${matchedSkills.map(s => `<span class="px-2 py-1 rounded-full text-xs bg-green-950/50 border border-green-800 text-green-300">${s}</span>`).join('') || '<span class="text-xs text-gray-500">No direct strengths detected.</span>'}</div>
          </section>

          <section class="bg-gray-950 border border-gray-800 rounded-2xl p-4">
            <p class="text-[0.7rem] uppercase tracking-widest text-red-400 font-bold">Missing Skills</p>
            <div class="mt-2 flex flex-wrap gap-1.5">${missingSkills.map(s => `<span class="px-2 py-1 rounded-full text-xs bg-red-950/50 border border-red-800 text-red-300">${s}</span>`).join('') || '<span class="text-xs text-gray-500">No missing skills detected.</span>'}</div>
          </section>

          <section class="bg-gray-950 border border-gray-800 rounded-2xl p-4 md:col-span-2">
            <p class="text-[0.7rem] uppercase tracking-widest text-yellow-400 font-bold">Improvement Plan</p>
            <div class="mt-2 space-y-1 text-sm text-gray-300">${improvement.map(i => `<p>${i}</p>`).join('') || '<p class="text-xs text-gray-500">No improvement plan needed.</p>'}</div>
          </section>

          <section class="rounded-2xl p-4 border md:col-span-2 ${color.chip}">
            <p class="text-[0.7rem] uppercase tracking-widest font-bold">Impact On Hiring</p>
            <p class="mt-2 text-sm">${deriveImpact(r, r.finalScore)}</p>
            <p class="mt-3 text-xs"><b>Recruiter Note:</b> ${deriveRecruiterNote(r)}</p>
          </section>
        </div>
      </article>`;
  }).join('');
}

function setActiveNav(page) {
  document.querySelectorAll('nav a').forEach(a => {
    a.classList.remove('bg-gray-800', 'text-white');
    a.classList.add('text-gray-400', 'hover:text-white');
  });
  const active = document.querySelector(`nav a[data-page="${page}"]`);
  if (active) {
    active.classList.add('bg-gray-800', 'text-white');
    active.classList.remove('text-gray-400', 'hover:text-white');
  }
}
