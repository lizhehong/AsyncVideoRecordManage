$(document).ready(function() {
	localLog = (function(){
		var contrainer = $("#logMessage")
		function Message(content,name){
			this.startTime = new Date();
			this.content = content;
			this.name = name;
		}
		function template(message){
			return $("<p>[Time:"+message.startTime+"]-["+message.name+"]-["+message.content+"]</p>")
		}
		function createMessage(content,name){
			return new Message(content,name)
		}
		function printf(message){
			if(contrainer){
				contrainer.append(template(message));
				contrainer.scrollTop( contrainer[0].scrollHeight );
			}
		}
		return {
			createMessage:createMessage,
			printf:printf
		}
	})();
	window.localLog = localLog;
});