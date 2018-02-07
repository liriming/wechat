/**
 * 分页控件：封装了翻页所需的参数、操作函数
 * 分页控件容器用“page”类标识，比如：<div class="pageDiv page"></div>
 * @author: XuJijun 
 */
(function(_win) {
	"use strict";
	
	//请求参数，保存了当前页数
	var pParam = {pageNo: 1, pageSize: 10, params: null, totalPage: 999};
	
	//指向搜索函数，用于翻页的时候回调搜索函数
	var search;
	
	var _p = {
		// 初始化调用
		init : function(obj, args) {
			return (function() {
				//console.log(obj);
				//console.log(args);
				_p.fillHtml(obj, args);
				_p.bindEvent(obj, args);
			})();
		},
        

		// 填充html
		fillHtml : function(obj, args) {
			var html = "<a href='javascript:;' id='firstPage'>首页</a>&nbsp;"
				+ "<a href='javascript:;' id='previousPage'>上一页</a>&nbsp;"
				+ "第<input type='number' id='pageNo' min='1' max='999' value='1'/>页<span id='totalPage'>（共<span id='pageCount'>999</span>页）</span>"
				+ "<a href='javascript:;' id='nextPage'>下一页</a>&nbsp;"
				+ "<a href='javascript:;' id='lastPage'>末页</a>";

			$(".page").html(html);
		},
        
        //绑定事件
        bindEvent: function (obj, args) {
        	$("#firstPage").on("click", function() {
				pParam.pageNo = 1;
				search();
			});
        	
        	$("#lastPage").on("click", function(){
        		pParam.pageNo = pParam.totalPage;
        		search();
        	});

        	$("#nextPage").on("click", function() {
				if (pParam.pageNo == pParam.totalPage) {
					alert("已经是最后一页了！");
					return;
				}
				pParam.pageNo++;
				search();
			});

        	$("#previousPage").on("click", function() {
				if (pParam.pageNo == 1) {
					alert("已经是第一页了！");
					return;
				}
				pParam.pageNo--;
				search();
			});
        	

        	// 页数控件失去焦点：
			$("#pageNo").blur(function() {
				var pn = Number($("#pageNo").val());
				if (pn < 1) {
					pn = 1;
					$("#pageNo").val(pn);
				} else if (pn > pParam.totalPage) {
					pn = pParam.totalPage;
					$("#pageNo").val(pn);
				}

				pParam.pageNo = pn;
				search();
			});

			// 页数回车：
			$("#pageNo").keyup(function(event) {
				if (event.which != 13) {
					return;
				}

				var pn = Number($("#pageNo").val());
				if (pn < 1) {
					pn = 1;
					$("#pageNo").val(pn);
				} else if (pn > pParam.totalPage) {
					pn = pParam.totalPage;
					$("#pageNo").val(pn);
				}

				pParam.pageNo = pn;
				search();
			});
        }
	};

	
	_win["page"] = {
		/**
		 * 初始化，创建分页控件
		 * @param options：每页记录数、搜索函数：{pageSize: 30, searchFunction: search}
		 */
		createPage: function(options){
			if(options.pageSize!=undefined){
				pParam.pageSize = options.pageSize;
			}
			search = options.searchFunction;
	        _p.init(this, pParam);
		},
		
		/**
		 * 把pageNo, pageSize和用户填写的查询条件build进pageParam中。
		 * 开始ajax查询数据前调用。
		 */
		buildParams: function(params){
			pParam.params = params;
			return pParam;
		},
		
		/**
		 * 重设pageNo为1。
		 * 重新开始查询的时候调用。
		 */
		resetPageNo: function() {
			pParam.pageNo = 1;
		},
		
		/**
		 * 更新分页控件中的信息
		 * @param data
		 */
		updatePage: function(data){
			if(data.totalPage==0){
				$("#totalPage").hide();
				$("#lastPage").hide();
			}else{
				pParam.totalPage = data.totalPage;
				$("#pageNo").attr("max", data.totalPage);
				$("#pageCount").html(data.totalPage);
			}
			$("#pageNo").val(data.pageNumber);
		},
	};
	
})(window);
