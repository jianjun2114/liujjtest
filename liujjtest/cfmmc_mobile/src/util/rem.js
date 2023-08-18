/**
 * Created by Lgw on 2017/5/3.
 */

(function (doc, win) {
  let docEl = doc.documentElement,
    resizeEvt = 'orientationchange' in window ? 'orientationchange' : 'resize',
    recalc = function () {
      let clientWidth = docEl.clientWidth;
      if (!clientWidth) return;
      if(clientWidth>=640){
        docEl.style.fontSize = '10px';
      }else{
        docEl.style.fontSize = 10 * (clientWidth / 375) + 'px';
      }
    };

  if (!doc.addEventListener) return;
  win.addEventListener(resizeEvt, recalc, false);
  doc.addEventListener('DOMContentLoaded', recalc, false);
})(document, window);
