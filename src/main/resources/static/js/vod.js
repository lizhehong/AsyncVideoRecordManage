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
		var playIndex = -1;
		var indexFile; 
		var curVodParam ;
		var applyPublishNextVodFlag = false;
		var tmpVideoCacheEventFlag = false;
		function initVideoEle(){
			myPlayer = videojs(videoEleId); //初始化视频
			$('#'+videoEleId).addClass('video-js');
			initEvent();//事件初始化
			initVideoPlugin();//插件初始化
		}
		function initVideoPlugin(){
			myPlayer.watermark({
			 	position: 'top-right',
			 	url: 'http://www.515cn.com',
			 	image: 'https://ss1.baidu.com/6ONXsjip0QIZ8tyhnq/it/u=3594708763,1434851869&fm=58',
			 	opacity: 0,
			 });
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
	    		if(!!indexFile && this.currentTime() > (indexFile.splitSecStep*0.1)){
	    			var nextVideo = getVideoList()[playIndex+1];
	    			if(!!nextVideo && !nextVideo.loaded){//视频缓存 让视频切换不卡
		    			cacheVideo(nextVideo);
		    		}else if(!nextVideo){
			    		//查看客户是否播放完毕 如果没有完毕则必须 缓存下个视频
			    		var thisVideo = getVideoList()[playIndex];
			    		if(!applyPublishNextVodFlag && thisVideo.endTime < curVodParam.endTime){
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
					//videoTag.hide();
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
	        myPlayer.src([{ src: u, type: applyVideoType }]);
	        myPlayer.play();
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