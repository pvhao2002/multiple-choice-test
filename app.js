(() => {
  const $ = s => document.querySelector(s);
  const $$ = s => [...document.querySelectorAll(s)];
  const letters = ["A","B","C","D","E","F"];
  let test = null, state = null, tick = null, allOpen = false;

  const homeScreen=$("#homeScreen"), examScreen=$("#examScreen"), resultSection=$("#resultSection");
  const timerWrap=$("#timerWrap"), timerEl=$("#timer"), submitTop=$("#submitTop"), changeSubjectBtn=$("#changeSubjectBtn");
  const startPanel=$("#startPanel"), examLayout=$("#examLayout"), questionList=$("#questionList"), navGrid=$("#navGrid");
  const progressText=$("#progressText"), confirmDialog=$("#confirmDialog"), confirmText=$("#confirmText");
  const storageKey=()=>test ? "mce_mock_"+test.id : "";

  function freshState(){
    return {answers:{},reviews:{},started:false,deadline:null,startedAt:null,student:""};
  }
  function save(){ if(test && state) localStorage.setItem(storageKey(),JSON.stringify(state)); }
  function load(){
    try{ state=JSON.parse(localStorage.getItem(storageKey()))||freshState(); }
    catch(e){ state=freshState(); }
  }
  function escapeHtml(s=""){
    return String(s).replace(/[&<>"']/g,m=>({"&":"&amp;","<":"&lt;",">":"&gt;","\"":"&quot;","'":"&#39;"}[m]));
  }
  function normalize(s=""){ return String(s).toLowerCase().trim().replace(/\s+/g," "); }
  function fmtScore(n){ return (Math.round((Number(n)+Number.EPSILON)*100)/100).toFixed(2).replace(/\.00$/,"").replace(/(\.\d)0$/,"$1"); }
  function formatTime(sec){
    sec=Math.max(0,Math.floor(sec)); const m=Math.floor(sec/60),s=sec%60;
    return String(m).padStart(2,"0")+":"+String(s).padStart(2,"0");
  }

  function goHome(){
    stopTimer(); test=null; state=null;
    homeScreen.classList.remove("hidden"); examScreen.classList.add("hidden"); resultSection.classList.add("hidden");
    timerWrap.classList.add("hidden"); submitTop.classList.add("hidden"); changeSubjectBtn.classList.add("hidden");
    window.scrollTo({top:0,behavior:"smooth"});
  }

  function selectSubject(key){
    test=window.MCE_TESTS[key]; load();
    homeScreen.classList.add("hidden"); resultSection.classList.add("hidden"); examScreen.classList.remove("hidden");
    changeSubjectBtn.classList.remove("hidden");
    $("#examKicker").textContent="ĐỀ LÀM THỬ 01 • "+test.subject.toUpperCase();
    $("#examTitle").textContent=test.title;
    $("#examSubtitle").textContent=test.subtitle;
    $("#examNotice").textContent=test.notice;
    $("#examStats").innerHTML=test.stats.map(([a,b])=>`<div><strong>${a}</strong><span>${escapeHtml(b)}</span></div>`).join("");
    $("#studentName").value=state.student||"";
    renderQuestions(); renderNav(); updateProgress();
    if(state.started && state.deadline){
      startPanel.classList.add("hidden"); examLayout.classList.remove("is-locked");
      timerWrap.classList.remove("hidden"); submitTop.classList.remove("hidden"); startTimer();
    } else {
      startPanel.classList.remove("hidden"); examLayout.classList.add("is-locked");
      timerWrap.classList.add("hidden"); submitTop.classList.add("hidden");
      $("#startBtn").textContent=Object.keys(state.answers||{}).length?"Tiếp tục làm bài":"Bắt đầu làm bài";
    }
    window.scrollTo({top:0,behavior:"smooth"});
  }

  function renderQuestions(){
    questionList.innerHTML=test.questions.map(q=>{
      let input="";
      if(q.type==="single"||q.type==="multi"){
        const inputType=q.type==="single"?"radio":"checkbox";
        const current=q.type==="multi"?(state.answers[q.n]||[]):state.answers[q.n];
        input=`<div class="options">`+q.options.map((op,i)=>{
          const l=letters[i],checked=q.type==="multi"?current.includes(l):current===l;
          return `<label class="option"><input type="${inputType}" name="q${q.n}" value="${l}" ${checked?"checked":""}><span class="option-letter">${l}</span><span class="option-text">${escapeHtml(op)}</span></label>`;
        }).join("")+`</div>`;
      } else if(q.type==="text"){
        input=`<input class="short-input" data-q="${q.n}" value="${escapeHtml(state.answers[q.n]||"")}" placeholder="Nhập câu trả lời">`;
      } else if(q.type==="pair"){
        const vals=Array.isArray(state.answers[q.n])?state.answers[q.n]:["",""];
        input=`<div class="pair-inputs">`+q.labels.map((lab,i)=>`<label><span>${escapeHtml(lab)}</span><input class="short-input pair-field" data-q="${q.n}" data-i="${i}" value="${escapeHtml(vals[i]||"")}" inputmode="numeric" placeholder="${escapeHtml(lab)}"></label>`).join("")+`</div>`;
      } else if(q.type==="essay"){
        input=`<textarea class="essay-input" data-q="${q.n}" placeholder="Nhập bài làm tại đây...">${escapeHtml(state.answers[q.n]||"")}</textarea>`;
      }
      const passage=q.passage?`<div class="passage">${escapeHtml(q.passage)}</div>`:"";
      const note=q.type==="multi"?`<div class="multi-note">Chọn tất cả đáp án đúng.</div>`:"";
      return `<article class="question-card" id="qcard-${q.n}">
        <div class="question-top"><span class="q-number">Câu ${q.n}</span><button class="review-toggle ${state.reviews[q.n]?"active":""}" data-review="${q.n}" type="button">★ Xem lại</button></div>
        ${passage}<div class="question-text">${escapeHtml(q.text)}</div>${note}${input}
      </article>`;
    }).join("");
    bindInputs();
  }

  function bindInputs(){
    $$('input[type="radio"]').forEach(el=>el.addEventListener("change",e=>{
      const n=Number(e.target.name.slice(1)); state.answers[n]=e.target.value; save(); updateProgress(); renderNav();
    }));
    $$('input[type="checkbox"]').forEach(el=>el.addEventListener("change",e=>{
      const n=Number(e.target.name.slice(1)); let arr=Array.isArray(state.answers[n])?[...state.answers[n]]:[];
      if(e.target.checked){ if(!arr.includes(e.target.value))arr.push(e.target.value); }
      else arr=arr.filter(x=>x!==e.target.value);
      state.answers[n]=arr; save(); updateProgress(); renderNav();
    }));
    $$(".short-input:not(.pair-field),.essay-input").forEach(el=>el.addEventListener("input",e=>{
      const n=Number(e.target.dataset.q); state.answers[n]=e.target.value; save(); updateProgress(); renderNav();
    }));
    $$(".pair-field").forEach(el=>el.addEventListener("input",e=>{
      const n=Number(e.target.dataset.q),i=Number(e.target.dataset.i); let arr=Array.isArray(state.answers[n])?[...state.answers[n]]:["",""];
      arr[i]=e.target.value; state.answers[n]=arr; save(); updateProgress(); renderNav();
    }));
    $$(".review-toggle").forEach(el=>el.addEventListener("click",e=>{
      const n=Number(e.currentTarget.dataset.review); state.reviews[n]=!state.reviews[n]; save(); e.currentTarget.classList.toggle("active",!!state.reviews[n]); renderNav();
    }));
  }

  function isAnswered(q){
    const a=state.answers[q.n];
    if(q.type==="multi") return Array.isArray(a)&&a.length>0;
    if(q.type==="pair") return Array.isArray(a)&&a.every(x=>normalize(x));
    return normalize(a||"").length>0;
  }
  function updateProgress(){
    const done=test.questions.filter(isAnswered).length;
    progressText.textContent=`${done}/${test.questions.length} đã làm`;
  }
  function renderNav(){
    navGrid.innerHTML=test.questions.map(q=>{
      const cls=[isAnswered(q)?"done":"",state.reviews[q.n]?"review":""].filter(Boolean).join(" ");
      return `<button class="nav-btn ${cls}" data-nav="${q.n}" type="button">${q.n}</button>`;
    }).join("");
    $$(".nav-btn").forEach(b=>b.addEventListener("click",()=>$("#qcard-"+b.dataset.nav)?.scrollIntoView({behavior:"smooth",block:"start"})));
  }

  function startExam(){
    state.student=$("#studentName").value.trim();
    if(!state.started){ state.started=true; state.startedAt=Date.now(); state.deadline=Date.now()+test.duration*1000; }
    save();
    startPanel.classList.add("hidden"); examLayout.classList.remove("is-locked");
    timerWrap.classList.remove("hidden"); submitTop.classList.remove("hidden");
    startTimer(); $("#qcard-1")?.scrollIntoView({behavior:"smooth",block:"start"});
  }
  function startTimer(){
    stopTimer();
    const update=()=>{
      const left=Math.ceil((state.deadline-Date.now())/1000);
      timerEl.textContent=formatTime(left);
      if(left<=0){ stopTimer(); finalizeSubmit(true); }
    };
    update(); tick=setInterval(update,1000);
  }
  function stopTimer(){ if(tick){clearInterval(tick);tick=null;} }

  function askSubmit(){
    const blanks=test.questions.filter(q=>!isAnswered(q)).length;
    confirmText.textContent=blanks?`Bạn còn ${blanks} câu chưa trả lời. Bạn vẫn muốn nộp bài?`:"Bạn đã trả lời tất cả câu hỏi. Nộp bài ngay?";
    if(confirmDialog.showModal) confirmDialog.showModal(); else if(confirm(confirmText.textContent)) finalizeSubmit(false);
  }

  function correctFor(q){
    const a=state.answers[q.n];
    if(q.type==="essay") return null;
    if(q.type==="single") return a===q.answer;
    if(q.type==="multi"){
      const x=Array.isArray(a)?[...a].sort().join(""):"",y=[...q.answer].sort().join(""); return x===y;
    }
    if(q.type==="text") return (q.answers||[]).some(v=>normalize(a)===normalize(v));
    if(q.type==="pair") return Array.isArray(a)&&q.answers.every((v,i)=>normalize(a[i])===normalize(v));
    return false;
  }
  function answerDisplay(q){
    const a=state.answers[q.n];
    if(q.type==="single") return a||"—";
    if(q.type==="multi") return Array.isArray(a)&&a.length?a.sort().join(", "):"—";
    if(q.type==="pair") return Array.isArray(a)?q.labels.map((x,i)=>x+": "+(a[i]||"—")).join("; "):"—";
    return normalize(a||"")?String(a):"—";
  }
  function correctDisplay(q){
    if(q.type==="single") return q.answer;
    if(q.type==="multi") return q.answer.join(", ");
    if(q.type==="pair") return q.labels.map((x,i)=>x+": "+q.answers[i]).join("; ");
    if(q.type==="text") return q.answers[0];
    return "Chấm theo hướng dẫn";
  }

  function finalizeSubmit(auto){
    if(!test||!state||!state.started) return;
    stopTimer();
    let score=0,correct=0,wrong=0,blank=0,pending=0;
    test.questions.forEach(q=>{
      if(!isAnswered(q)){ if(q.type==="essay")pending++; else blank++; return; }
      const c=correctFor(q);
      if(c===null){pending++;return;}
      if(c){score+=q.points;correct++;}else wrong++;
    });
    score=Math.min(test.autoMax,score);
    const remaining=Math.max(0,Math.ceil((state.deadline-Date.now())/1000));
    const used=Math.max(0,test.duration-remaining);
    examScreen.classList.add("hidden"); resultSection.classList.remove("hidden");
    timerWrap.classList.add("hidden"); submitTop.classList.add("hidden"); changeSubjectBtn.classList.remove("hidden");
    $("#scoreValue").textContent=fmtScore(score);
    $("#scoreDenom").textContent=test.autoMax===10?"/ 10":"/ 6 tự động";
    $("#resultTitle").textContent=auto?"Hết giờ – bài đã được nộp!":"Hoàn thành bài thi!";
    $("#resultMeta").textContent=`${state.student?state.student+" • ":""}${test.subject} • Thời gian làm: ${formatTime(used)}`;
    $("#resultChips").innerHTML=`
      <span class="chip good">✓ <b>${correct}</b> câu đúng</span>
      <span class="chip bad">× <b>${wrong}</b> câu sai</span>
      <span class="chip neutral">○ <b>${blank}</b> chưa làm</span>
      ${pending?'<span class="chip pending">✎ <b>'+pending+'</b> câu chờ chấm</span>':""}`;
    $("#resultNote").textContent=test.id.startsWith("vietnamese")
      ?`Điểm trắc nghiệm tự động: ${fmtScore(score)}/6. Hai câu tự luận chiếm 4 điểm và cần chấm theo rubric bên dưới; website không tự gán điểm để tránh đánh giá sai bài viết.`
      :`Điểm tự động: ${fmtScore(score)}/10. Mở từng câu bên dưới để xem đáp án và lời giải.`;
    renderReview();
    localStorage.removeItem(storageKey());
    window.scrollTo({top:0,behavior:"smooth"});
  }

  function renderReview(){
    $("#reviewList").innerHTML=test.questions.map(q=>{
      const answered=isAnswered(q),c=correctFor(q);
      let statusClass="pending",status="Chờ chấm";
      if(c===true){statusClass="good";status="✓ Đúng";}
      else if(c===false){statusClass="bad";status=answered?"✕ Sai":"○ Chưa làm";}
      const rubric=q.rubric?`<div class="rubric">${escapeHtml(q.rubric)}</div>`:"";
      return `<div class="review-item">
        <div class="review-head"><strong>Câu ${q.n}</strong><span class="review-status ${statusClass}">${status}</span></div>
        <div class="answer-line">Bài làm: <b>${escapeHtml(answerDisplay(q))}</b> • Đáp án/chấm: <b>${escapeHtml(correctDisplay(q))}</b></div>
        ${rubric}
        <details><summary>Xem lời giải / gợi ý</summary><div class="solution">${escapeHtml(q.solution||"")}</div></details>
      </div>`;
    }).join("");
  }

  function clearCurrent(){
    if(!test) return;
    if(confirm("Xóa toàn bộ đáp án và thời gian của đề "+test.subject+"?")){
      localStorage.removeItem(storageKey()); state=freshState(); stopTimer(); renderQuestions(); renderNav(); updateProgress();
      startPanel.classList.remove("hidden"); examLayout.classList.add("is-locked"); timerWrap.classList.add("hidden"); submitTop.classList.add("hidden"); timerEl.textContent="40:00";
    }
  }

  $$(".subject-card").forEach(b=>b.addEventListener("click",()=>selectSubject(b.dataset.subject)));
  $("#startBtn").addEventListener("click",startExam);
  submitTop.addEventListener("click",askSubmit); $("#submitBottom").addEventListener("click",askSubmit);
  $("#confirmSubmitBtn").addEventListener("click",()=>setTimeout(()=>finalizeSubmit(false),0));
  $("#clearBtn").addEventListener("click",clearCurrent);
  changeSubjectBtn.addEventListener("click",goHome); $("#brandHome").addEventListener("click",e=>{e.preventDefault();goHome();});
  $("#backHomeBtn").addEventListener("click",goHome);
  $("#retryBtn").addEventListener("click",()=>{const id=test.id; const key=Object.keys(window.MCE_TESTS).find(k=>window.MCE_TESTS[k].id===id); state=freshState(); resultSection.classList.add("hidden"); selectSubject(key);});
  $("#toggleAllSolutions").addEventListener("click",()=>{
    allOpen=!allOpen; $$("#reviewList details").forEach(d=>d.open=allOpen);
    $("#toggleAllSolutions").textContent=allOpen?"Đóng tất cả lời giải":"Mở tất cả lời giải";
  });
})();