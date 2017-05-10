$(document).ready(function() {
	function VodParam(){
		this.monitorId = $('#hidid').val();
		this.startTime = new Date($('#startTime').val());
		this.endTime = new Date($("#endTime").val());
	}
	var localVideoList = (function(){
		var videoList = [];
		var videoEleId = "example-video";//真正视频标签
		var applyVideoType = "video/mp4";//视频格式
		var tmpVideoEleId = "tmpVideo";//临时视频
		var myPlayer;
		var playIndex = -1;
		var indexFile; 
		var publishVodFlag = false;
		var curVodParam ;
		function initVideoEle(){
			myPlayer = videojs(videoEleId); //初始化视频
			$('#'+videoEleId).addClass('video-js');
			initEvent();//事件初始化
		}
		function initEvent(){
			//视频播放停止事件
			myPlayer.on("ended",function(){
	    		var video = videoList[playIndex++];
	    		if(!!video){
	    			playVideo(video)
	    		}
	    		
	    	});
			//视频播放进度事件
	    	myPlayer.on("timeupdate",function(){
	    		if(!!indexFile && this.currentTime() > (indexFile.splitSecStep*0.6)){
	    			var nextVideo = getVideoList()[playIndex+1];
	    			if(!!nextVideo && !nextVideo.loaded){//视频缓存 让视频切换不卡	
		    			nextVideo.loaded = true;//相当于自锁
		    			cacheVideo(nextVideo);
		    		}else if(!nextVideo){
			    		//查看客户是否播放完毕 如果没有完毕则必须 缓存下个视频
			    		var thisVideo = getVideoList()[playIndex];
			    		if(thisVideo.endTime < curVodParam.endTime){
			    			//通知服务器
			    			publish_vod({
			    				param:{
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
			if(!tmpVideo){
				var videoTag = $("<video  id='"+tmpVideoEleId+"'src='"+"/"+curVodParam.monitorId+"/"+video.fileName+"'></video>");
				videoTag.hide();
				$("body").append(videoTag);
			}else{
				tmpVideo.src="/"+curVodParam.monitorId+"/"+video.fileName;
				tmpVideo.load();
			}
		}
		//视频播放
		function playVideo(video) {
	        //播放视频
			if(!!myPlayer)
				initVideoEle();
			while(video.downLoadState != "已经转码");//阻塞检测
	        var u = '/' + curVodParam.monitorId + '/' + video.fileName;
	        myPlayer.src([{ src: u, type: applyVideoType }]);
	        myPlayer.play();
	  
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
			playIndex = 0;
			playVideo(getVideoList()[playIndex]);
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
			$.ajax($.extends({},ajaxParam,param));
		}
		function removeAll(){
			videoList = [];
			playIndex = -1;
		}
		function refreshVodParam(){
			curVodParam = new VodParam();
			return curVodParam;
		}
		function refreshIndexFile() {
			if(publishVodFlag){
				removeAll();
				setPublishVodFlag(false);
				refreshVodParam();
			}
			
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
						if(
								video.vodReqState =="已经请求" //切记这个状态 因为视频列表是用户共享的
								&& 
								curVodParam.startTime.getTime() <= new Date(video.startTime).getTime()
								&&//只拿客户点播的视频时间段内视频列表
								curVodParam.endTime.getTime() >= new Date(video.endTime).getTime())
						{
							localVideoList.saveVideoItem(video);
						}
					}
					if(playIndex == -1)
						startPlayVideo();
				}
			});
		}
		function setPublishVodFlag(TrueOrFalse){
			publishVodFlag = TrueOrFalse;
		}
		return {
			playVideo:playVideo,
			saveVideoItem:saveVideoItem,
			findVideoInCache:findVideoInCache,
			getVideoList:getVideoList,
			refreshIndexFile:refreshIndexFile,
			publish_vod:publish_vod,
			refreshVodParam:refreshVodParam,
			setPublishVodFlag:setPublishVodFlag
		}
		
	})();

	window.localVideoList = localVideoList;
	
	var intInterval = 0;
	$("#goBtn").on("click",function() {
		localVideoList.setPublishVodFlag(true);
		var vodParam = localVideoList.refreshVodParam();
		localVideoList.publish_vod({
			data : vodParam,
			success : function(data) {
				if(!intInterval)
					intInterval = window.setInterval(localVideoList.refreshIndexFile, 1000);
			}
		});
	});
});