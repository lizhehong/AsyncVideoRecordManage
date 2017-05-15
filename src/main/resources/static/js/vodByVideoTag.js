$(document).ready(function() {
	function VodParam(){
		this.monitorId = $('#hidid').val();
		this.startTime = $('#startTime').val();
		this.endTime = $("#endTime").val();
	}
	var localVideoList = (function(){
		var videoList = [];
		var videoEleId = "example-video";//真正视频标签
		var applyVideoType = "video/mp4";//视频格式
		var tmpVideoEleId = "tmpVideo";//临时视频
		var myPlayer;
		var myPlayerJsObject;
		var playIndex = -1;
		var indexFile; 
		var curVodParam ;
		var applyPublishNextVodFlag = false;
		var tmpVideoCacheEventFlag = false;
		var startSpeedUp = false;
		var videoSpeedUpNum = new Number($("#videoSpeedUpNum").val());
		var openSpeedUpFlg = videoSpeedUpNum > 0;
		var vodSpeedUpInterval = -1; 
		var mainVideoCanplayFlag = false;
		function initVideoEle(){
			myPlayer = $("#"+videoEleId); //初始化视频
			myPlayerJsObject = document.querySelector("#"+videoEleId);
			if(localUtils.isMobile() || !openSpeedUpFlg){
				myPlayer.prop("controls","controls")	
			}else{
				//非移动端才能快进
				openSpeedUpFlg > 0?openVideoSpeedUp(videoSpeedUpNum):null;
			}
			initEvent();//事件初始化
			initVideoPlugin();//插件初始化
		}
		function lanchFullScreen(ele){
			if(ele.requestFullscreen) {  
				ele.requestFullscreen();  
			} else if(ele.mozRequestFullScreen) {  
				ele.mozRequestFullScreen();  
			} else if(ele.webkitRequestFullscreen) {  
				ele.webkitRequestFullscreen();  
			} else if(ele.msRequestFullscreen) {  
				ele.msRequestFullscreen();  
			}
		}
		//视频加速播放
		function openVideoSpeedUp(){
			if(!startSpeedUp){
				myPlayer.dblclick(function(){
					lanchFullScreen(myPlayerJsObject);
				});
				vodSpeedUpInterval =window.setInterval(function(){
					myPlayerJsObject.currentTime = myPlayerJsObject.currentTime+(videoSpeedUpNum-0.5);
				},1000);
				startSpeedUp = true;
			}
		}
		function colseVideoSpeedUp(){
			vodSpeedUpInterval?clearInterval(vodSpeedUpInterval):null;
		}
		function initVideoPlugin(){
			
		}
		function initEvent(){
			//视频播放停止事件
			myPlayer.on("ended",function(){
	    		var video = videoList[++playIndex];
	    		if(!!video){
	    			var message = localLog.createMessage("开始时间："+video.startTime +"-结束时间："+ video.endTime,"开始播放视频");
					localLog.printf(message)
	    			playVideo(video);
	    			applyPublishNextVodFlag = false;
	    		}
	    		
	    	});
			//视频播放进度事件
	    	myPlayer.on("timeupdate",function(){
	    		//localLog.printf(localLog.createMessage(this.currentTime(),"当前文件进度"))
	    		if(!!indexFile && this.currentTime > (indexFile.splitSecStep*0.1)){
	    			var nextVideo = getVideoList()[playIndex+1];
	    			if(!!nextVideo && !nextVideo.loaded){//视频缓存 让视频切换不卡
		    			cacheVideo(nextVideo);
		    		}else if(!nextVideo){
			    		//查看客户是否播放完毕 如果没有完毕则必须 缓存下个视频
			    		var tmpPalyIndex = playIndex;
			    		var list = getVideoList();
			    		var thisVideo = tmpPalyIndex>0?list[tmpPalyIndex]:null;
			    		//出现网络阻塞的情况下 
			    		for(;!thisVideo;tmpPalyIndex--){
			    			thisVideo = list[tmpPalyIndex];
			    		}
			    		if(!applyPublishNextVodFlag && thisVideo.endTime+(tmpPalyIndex*indexFile.splitSecStep) < curVodParam.endTime){
			    			var message = localLog.createMessage("开始时间："+thisVideo.endTime +"-结束时间："+ curVodParam.endTime,"异步通知服务器缓存视频开始");
							localLog.printf(message)
			    			applyPublishNextVodFlag = true;
			    			//通知服务器
			    			publish_vod({
			    				data:{
			    					monitorId:curVodParam.monitorId,
			    					startTime:thisVideo.endTime,//偏移时间
			    					endTime:curVodParam.endTime
			    				}
			    			});
			    		}
			    		
		    		}
	    		}
	    	});
		}
		//通过视频的视频找到视频
		function findVideoInCache(video){
			//先排序 
			updateVideoSort("asc");
			//
			for(var i=0;i<videoList.length;i++){
				if(videoList[i].fileName == video.fileName ){
					newVideo = videoList[i];
					return newVideo;
				}
			}
			
			return null;
		}
		//添加一个视频列表单元
		function saveVideoItem(video){
			var oldVideo = findVideoInCache(video);
			if(!!!oldVideo)
				videoList.push(video);
			else{
				oldVideo.downLoadState = video.downLoadState;//更新客户端视频状态
			}
			return oldVideo;
		}
		//更新视频列表排序
		function updateVideoSort(direct){
			if(videoList.length > 0){
				videoList.sort(function(v1,v2){
					if(direct == "asc")
						return new Date(v1.startTime).getTime() -  new Date(v2.startTime).getTime();
					else
						return new Date(v2.startTime).getTime() -  new Date(v1.startTime).getTime();
				});
			}
		}
		//缓存视频
		function cacheVideo(video){
			var tmpVideo = document.querySelector("#"+tmpVideoEleId);
			try{
				if(!tmpVideo){
					var videoTag = $("<video  style='width:300px;height:300px;'id='"+tmpVideoEleId+"'src='"+"/"+curVodParam.monitorId+"/"+video.fileName+"'></video>");
					$("body").append(videoTag);
					tmpVideo = document.querySelector("#"+tmpVideoEleId);
				}else{
					tmpVideo.src="/"+curVodParam.monitorId+"/"+video.fileName;
					tmpVideo.load();
				}
				video.loaded = true;//相当于自锁
				
			}catch(err){
				var message = localLog.createMessage("开始时间："+video.startTime +"-结束时间："+ video.endTime,"缓存视频列表【错误】");
				localLog.printf(message)
				video.loaded = false;
			}finally{
				if(tmpVideo && !tmpVideoCacheEventFlag){
					localLog.printf(localLog.createMessage("播放事件","设置临时播放器"))
					tmpVideo.oncanplay = function(){
						var message = localLog.createMessage("开始时间："+video.startTime +"-结束时间："+ video.endTime,"缓存视频成功");
						localLog.printf(message)
					}
					tmpVideoCacheEventFlag = true;
				}
			}
		}
		//视频播放
		function playVideo(video) {
	        //播放视频
			if(!myPlayer)
				initVideoEle();
			//while(video.downLoadState != "已经转码");//阻塞检测
	        var u = '/' + curVodParam.monitorId + '/' + video.fileName;
	        myPlayer.prop("src",u);
	        if(!mainVideoCanplayFlag){
	        	mainVideoCanplayFlag = true;
		        myPlayerJsObject.oncanplay = function(){
		        	 myPlayerJsObject.play();
		        }
	        }
	        initVideoPlugin();
	  
		}
		/**
		 * [getVideoList 得到视频列表]
		 * @return {[type]} [description]
		 */
		function getVideoList(){
			updateVideoSort("asc");
			return videoList;
		}
		//开始播放第一个视频
		function startPlayVideo(){
			var tmpIndex = 0;
			var video = getVideoList()[tmpIndex];
			if(video && video.downLoadState == "已经转码"){
				playIndex = tmpIndex;
				playVideo(video);
			}
		}
		//订阅一个点播
		function publish_vod(param){
			var ajaxParam = {
				type : 'post',
				async : false,
				url : "/monitor/publish_vod",
				cache : false
			}
			
			// 首先开启播放
			$.ajax($.extend({},ajaxParam,param));
		}
		function removeAll(){
			videoList = [];
			playIndex = -1;
		}
		function refreshVodParam(){
			curVodParam = new VodParam();
			return curVodParam;
		}
		//yyyy-MM-dd HH:mm:ss
		function commomDateFormat(dateStr){
			return new Date(dateStr .substr(0,10)+"T"+dateStr .substr(11,8))
		}
		function refreshIndexFile() {
			
			$.ajax({
				type : 'get',
				async : false,
				url : "/" + curVodParam.monitorId + "/index.json",
				cache : false,
				success : function(data) {
					indexFile = data;
					var videoList = data.videos;
					for (var i = 0; i < videoList.length; i++) {
						var video = videoList[i];
						var flag = video.vodReqState =="已经请求" //切记这个状态 因为视频列表是用户共享的
						//只拿客户点播的视频时间段内视频列表
						var flag1 = commomDateFormat(curVodParam.startTime).getTime() <= commomDateFormat(video.startTime).getTime();
						var flag2 = commomDateFormat(curVodParam.endTime).getTime() >= commomDateFormat(video.endTime).getTime();
								
						
						if(flag && flag1 && flag2){
							localVideoList.saveVideoItem(video);
						}
					}
					if(playIndex == -1)
						startPlayVideo();
				}
			});
		}

		return {
			playVideo:playVideo,
			saveVideoItem:saveVideoItem,
			findVideoInCache:findVideoInCache,
			getVideoList:getVideoList,
			refreshIndexFile:refreshIndexFile,
			publish_vod:publish_vod,
			refreshVodParam:refreshVodParam,
			//setPublishVodFlag:setPublishVodFlag
		}
		
	})();

	window.localVideoList = localVideoList;
	
	//刷新参数
	localVideoList.refreshVodParam();
	
	var intInterval = window.setInterval(localVideoList.refreshIndexFile, 1000);
	
});