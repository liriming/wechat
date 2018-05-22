/**
 * 函数封装到sys对象中，避免全局变量污染
 */
(function(_win, $) {
	"use strict";
	
	var projectName = "";
	var loginUrl = "login.html";

	var returnCode = {
		loginError : "400",
		success : "200"
	};

	// 获取项目根路径，如： http://localhost:8083
	var getHost = function(){
		//获取当前网址，如： http://localhost:8083/ysb/web/index.html
		var curWwwPath = window.document.location.href;
		//获取主机地址之后的目录，如：ysb/web/index.html
		var pathName = window.document.location.pathname;
		var pos = curWwwPath.indexOf(pathName);
		//获取主机地址，如： http://localhost:8083
		return curWwwPath.substring(0, pos);
	};

    var _fn = (function () {
    	/**
    	 * 把form中的所有name和value拼装在一个对象里面。可以用来自动拼装Ajax调用的参数。
    	 * 用法：params = $("form").serializeObject();
    	 */
    	$.fn.serializeObject = function() {
    		/**
    		 * 原理：serializeArray()把form转换成一个数组：
    		 * [ { name: "myName",
    		 *     value: 6
    		 *   }
    		 *   ...
    		 * ]
    		 * 
    		 * Array.prototype.reduce()再循环上面这个数组，比如把第一个元素变成：{myName: 6}
    		 * reduce的最后那个参数{}作为回调函数的第一个参数（a）的初始值。
    		 * 参考：https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/Reduce
    		 */
    		//return this.serializeArray().reduce(function(a, x) { a[x.name] = x.value; return a; }, {});

    		//IE8不支持reduce()！！，只能改成下面这种了：
    		var obj=new Object();  
   		    $.each(this.serializeArray(), function(index, param) {
				if(!(param.name in obj)) {
					obj[param.name] = param.value;
				}
			});
   		    
   		    return obj;
    	};
    })();
	
	_win["sys"] = {
		
		/**
		 * 获取API URL的根，比如http://api.ysbang.cn/ysb
		 */
		getApiRoot: function(){
			return getHost() + projectName;
		},
		
		/**
		 * 发送ajax请求，为了便于扩展，使用option对象作为入参
		 * 用法：var options={url:"/servlet/abc", params: {}, callback: function myCallback(){}, ...}; sys.ajax(options);
		 */
		ajax: function(options){
			var _defaultOpts = {//定义缺省的参数和回调函数
				debug: false,
				method: "POST",
				url: "",
				params: {authcode: "123456", platform: "web", version: "web1.0"},
				dataType: "json",
				contentType : "application/json;charset=utf-8",
				cache: false,
				timeout: 350000,
				callback: function() {
					if(result.code == returnCode.loginError){
						if(confirm(result.message)){
							top.location.href = loginUrl;
							return;
						}
					}
				},
				errorCallback: function(result) {if(result){alert(result.message);}},
				completeCallback: function() {},
				//sysError: function() {},
				showLoadIcon: false
			};
			
			var _p = $.extend(_defaultOpts.params, options.params); //提前获取参数，以免被下一句覆盖掉
			var opts = $.extend(_defaultOpts, options);
			
			var _url = (opts.url.indexOf("http") == 0) ? opts.url : sys.getApiRoot() + opts.url;
			
			//var _params = (typeof opts.params == "string") ? JSON.parse(opts.params) : opts.params;
			//_params.authcode = _params.authcode || "123456";
			//_params.platform = "web";
			//_params.version = "web1.0";
			_p = JSON.stringify(_p);
			
			$.ajax({
				method : opts.method,
				url : _url,
				contentType : opts.contentType,
				dataType : opts.dataType, // 表示返回值类型
				data : _p,
				success : function _successCallback(result) {
					if(result.code == returnCode.loginError){
						if(confirm(result.message)){
							top.location.href = loginUrl;
							return;
						}
					}
					
					if(result.code == returnCode.success){
						opts.callback(result);
					}else{
						opts.errorCallback(result);
					}
				},
				error : function _error(jqXHR, status, errorThrown) {
					if(opts.debug===true){
						alert("发生了错误：" + JSON.stringify(status) + "，" + JSON.stringify(errorThrown));
					}
					opts.errorCallback();
				},
				complete: function _completeCallback(){
					opts.completeCallback();
				}
			});
		},
		
		/**
		 * 获取URL中参数的值
		 * 
		 * 例子：http://abc.com?action=update&id=987654321789
		 * var action = getUrlParam("action"); //返回action的值为"update"
		 * 
		 * @Param: name: 要获取的参数名字
		 * @param: _location：可选参数，页面的URL，在弹出窗口中使用
		 * @return：返回参数的值
		 */
		getUrlParam: function(name, _location){
			var _location_url =_location || window.location.search; //window.location.search：URL中问号及其后面的内容
			var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
			var r = decodeURI(_location_url.substr(1)).match(reg); //匹配目标参数
			
			return (r == null) ? null : unescape(r[2]);
		},
		/**
		 * 保存userToken到localStorage
		 * @param userToken
		 */
		saveUserToken : function(userToken) {
			if (userToken === null) { return; }
			localStorage.userToken = userToken;
		},
		/**
		 * 从localStorage获取之前保存过的userToken
		 * @returns
		 */
		getSavedUserToken : function() {
			return localStorage.userToken;
		},
		/**
		 * F5只刷新子iframe
		 * @param iframeId
		 * @param url
		 */
		refresh : function(iframeId,url){
			$(document).keydown(function(e){
				var ev = window.event || e;
				var code = ev.keyCode || ev.which;
				if (code == 116) {
					if(url!=null&&url!=""&& typeof url != "undefined"){
			    		window.parent.document.getElementById(iframeId).src = url;
			    	}else{
			    		window.parent.document.getElementById(iframeId).src = window.parent.document.getElementById(iframeId).src; 
			    	}
					if(ev.preventDefault) {
						ev.preventDefault();
					} else {
						ev.keyCode = 0;
						ev.returnValue = false;
					}
				}
			    //监听F5键
			   /* if(e.keyCode==116){
			    	if(url!=null&&url!=""&& typeof url != "undefined"){
			    		window.parent.document.getElementById(iframeId).src = url;
			    	}else{
			    		window.parent.document.getElementById(iframeId).src = window.parent.document.getElementById(iframeId).src; 
			    	}
			    	return false;
			    }*/
			});
		}
	};
})(window, $);


/**
 * 判断字符串是否为空
 */
function isEmpty(str) {
	return ""==str||"undefined"==typeof(str)||null==str;
}

/**
 * 把html保留符号（<、>、'、"）替换为html编码
 * @param input
 * @returns {String}
 */
function replaceHtml(input){
	if(input==null){
		return "";
	}
	var result = ""; 
	var reg1 = new RegExp("<","gi");
	var reg2 = new RegExp(">","gi");
	var reg3 = new RegExp("'","gi");
	var reg4 = new RegExp("\"","gi");
	result = input.replace(reg1,"&lt;");
	result = result.replace(reg2, "&gt;");
	result = result.replace(reg3, "&#39;");
	result = result.replace(reg4, "&#34;");
	return result;
}