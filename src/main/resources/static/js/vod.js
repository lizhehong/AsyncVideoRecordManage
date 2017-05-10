$(document).ready(function() {
	var intInterval = 0;
	var localVideoList = (function(){
		var videoList = [];
		var videoEleId = "example-video";//真正视频标签
		var applyVideoType = "video/mp4";//视频格式
		var tmpVideoEleId = "tmpVideo";//临时视频
		var videoIdTag = $('#hidid');
		var myPlayer;
		var playIndex = -1;
		var indexFile;
		function initVideoEle(){
			myPlayer = videojs(videoEleId); //初始化视频
			$('#'+videoEleId).addClass('video-js');
			initEvent();//事件初始化
		}
		function initEvent(){
			//视频播放停止事件
			myPlayer.on("ended",function(){
	    		var video = videoList[playIndex];
	    		if(!!video){
	    			playVedio(videoIdTag.val(),video.fileName)
	    		}
	    		//通知客户端 已经看完了多少视频 由服务器 去做处理
	    	});
			//视频播放进度事件
	    	myPlayer.on("timeupdate",function(){
	    		var video = videoList[playIndex];
	    		if(!!indexFile && this.currentTime() > (indexFile.splitSecStep*0.6) && !!video && !video.loaded){//视频缓存 让视频切换不卡	
	    			video.loaded = true;//相当于自锁
	    			cacheVideo(video);
	    		}
	    	})
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
				var videoTag = $("<video  id='"+tmpVideoEleId+"'src='"+"/"+videoIdTag.val()+"/"+video.fileName+"'></video>");
				videoTag.hide();
				$("body").append(videoTag);
			}else{
				tmpVideo.src="/"+videoIdTag.val()+"/"+video.fileName;
				tmpVideo.load();
			}
		}
		//视频播放
		function playVedio(id, filename) {
	        //播放视频
			if(!!myPlayer)
				initVideoEle()
	        var u = '/' + id + '/' + filename;
	        myPlayer.src([{ src: u, type: applyVideoType }]);
	        myPlayer.play();
	  
		}
		function getVideoList(){
			updateVideoSort("asc");
			return videoList;
		}
		function refreshIndexFile() {
			$.ajax({
				type : 'get',
				async : false,
				url : "/" + videoIdTag.val() + "/index.json",
				cache : false,
				success : function(data) {
					indexFile = data;
					var videoList = data.videos;
					successedMaxNum = data.successedNum;
					var newVideoList = [];
					for (var i = 0; i < videoList.length; i++) {
						var video = videoList[i];
						var clientStartTime = new Date($('#startTime').val());
						var clientEndTime = new Date($('#endTime').val());
						if(
								video.vodReqState =="已经请求" //切记这个状态 因为视频列表是用户共享的
								&& 
								clientStartTime.getTime() <= new Date(video.startTime).getTime()
								&&//只拿客户点播的视频时间段内视频列表
								clientEndTime.getTime() >= new Date(video.endTime).getTime())
						{
							localVideoList.saveVideoItem(video);
						}
					}
				}
			});
		}
		return {
			playVedio:playVedio,
			saveVideoItem:saveVideoItem,
			findVideoInCache:findVideoInCache,
			getVideoList:getVideoList,
			refreshIndexFile:refreshIndexFile
		}
		
	})();

	window.localVideoList = localVideoList;
	//视频播放
	
	$("#goBtn").on("click",function() {
		var param = {
			monitorId : $('#hidid').val(),
			startTime : $('#startTime').val(),
			endTime : $("#endTime").val()
		};
		// 首先开启播放
		$.ajax({
			type : 'post',
			async : false,
			url : "/monitor/publish_vod",
			data : param,
			// dataType: 'jsonp',
			cache : false,
			success : function(data) {
				if(!intInterval)
					intInterval = window.setInterval(localVideoList.refreshIndexFile, 1000);
			}
		});
	});
});