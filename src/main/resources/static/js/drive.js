localUtils = (function(){
	var u = navigator.userAgent;
	function isAndroid(){
		return u.indexOf('Android') > -1 || u.indexOf('Adr') > -1; //android终端
	}
	function isIos(){
		return !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
	}
	function isMobile(){
		return isAndroid() || isIos()
	}
	return {
		isAndroid:isAndroid,
		isIos:isIos,
		isMobile:isMobile
	}
})();