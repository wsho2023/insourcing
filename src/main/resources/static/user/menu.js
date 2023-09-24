  	const btn = document.querySelector('.btn-menu');
  	const nav = document.querySelector('nav');

  	btn.addEventListener('click', btnClick, false);
  	function btnClick() {
  	  	nav.classList.toggle('open-menu');
  	  	if (btn.innerHTML == 'メニュー') {
  	  	  	btn.innerHTML = '閉じる　';
  	  	} else {
  	  	  	btn.innerHTML = 'メニュー';
  	  	}
  	};
  	
