var intInterval = 0;
var indexFile;
$(document).ready(function() {
		var successVideoList = [];
		var nowIndex = 0;
		var hasPauseEvent = false;
		var firstOk = false;
		function findVideoByFileNameInCache(fileName){
			var video ;
			$.each(successVideoList,function(key,val){
				if(this.fileName == fileName ){
					video = this;
					return false;
				}
			});
			return video;
		}
		//视频播放
		function playVedio(id, filename) {
			$('#example-video').addClass('video-js');
            $('#hidid').val(id);
            //播放视频
            var myPlayer = videojs("example-video");  //初始化视频
            var u = '/' + id + '/' + filename;
            myPlayer.src([{ src: u, type: 'video/flv' }]);
            //myPlayer.load(u);  //使video重新加载
            myPlayer.play();
            nowIndex++;
            if(!hasPauseEvent){//检测播放进度
            	hasPauseEvent = true;
            	myPlayer.on("ended",function(){
            		var video = successVideoList[nowIndex];
            		if(!!video){
            			playVedio($('#hidid').val(),video.fileName)
            		}
            		//通知客户端 已经看完了多少视频 由服务器 去做处理
            	})
            }
		}
		// 计算已经下载的文件数
		window.refreshIndexFile = function(monitorId) {
			$.ajax({
				type : 'get',
				async : false,
				url : "/" + monitorId + "/index.json",
				cache : false,
				success : function(data) {
					indexFile = data;
					var videoList = data.videos;
					successedMaxNum = data.successedNum;
					for (var i = 0; i < videoList.length; i++) {
						var video = videoList[i];
						if(video.downLoadState =="已经转码" && !findVideoByFileNameInCache(video.fileName)){
							successVideoList.push(video);
							if(successVideoList[0] && !firstOk){
								firstOk = true;
								playVedio($('#hidid').val(),video.fileName);
							}
						}
					}
				}
			});
		}
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
					var monitorId = data.parentPathName;
					intInterval = window.setInterval("refreshIndexFile('" + monitorId + "')", 1000);
				}
			});
			

		});
	}
);