/*
 * author: Tracy
 * email: cuixigame@gmail.com
 * date: 2014.10.28
 */

;(function($){

	$.fn.tab = function(options){

		// 默认参数
		var defaults = {
			defaultIndex	: 0,					// Number 默认选中项
			trigger			: "click",				// String 触发tab切换事件类型
			auto 			: false,				// Boolean tab是否自动切换
			duration		: 400,					// Number 动画运动时间
			interval 		: 3000,					// Number tab自动切换时间间隔
			delay			: 200,					// Number 鼠标滑动延迟
			tabMenu         : '.tab_menu',			// String tabs菜单选择器
			tabMenuItem		: 'li',					// String tabs菜单项选择器
			tabContent 		: '.tab_content',		// String tabs内容外层选择器
			tabCon 			: '.tab_con',			// String tabs内容列表选择器
			tabConItem 		: '.tab_conItem',		// String tabs内容列表项选择器
			easing			: "swing",				// String 缓动效果
			effects			: "normal",				// String tab切换效果
			dataName		: "data-index",			// String 选项卡索引
            callback       : function(){}          // Function 回调函数    
		};

		if(this.length == 0){
			return this;
		}else if(this.length > 1){
			this.each(function(){
				$(this).tab(options);
			});
			return this;
		}

		var tab = this;
		tab.plugin = this;
		tab.config = {};
		tab.timer = null;


		var init = function(options){
			// 合并参数
			tab.config = $.extend({}, defaults, options);

			tab.menu = tab.plugin.find(tab.config.tabMenu);
			tab.menuItem = tab.menu.find(tab.config.tabMenuItem);
			tab.len = tab.menuItem.length;
			tab.content = tab.plugin.find(tab.config.tabContent);
			tab.con = tab.content.find(tab.config.tabCon);
			tab.conItem = tab.con.find(tab.config.tabConItem);
			tab.elemWidth = tab.conItem.width();
			tab.elemHeight = tab.conItem.height();

			// 初始化样式
			initLayout();

			// 初始化事件
			initAffair();

			if(tab.config.auto && tab.len > 1){
				autoTab();
			}
		};

		// 样式初始化
		var initLayout = function(){
			var defaultIndex = tab.config.defaultIndex,
				effects = tab.config.effects,
				elemWidth = tab.elemWidth,
				elemHeight = tab.elemHeight,
				size, sizeType, floatType, posType, distance;

			if(effects == "normal"){
				tab.conItem.css("display","none");
				tab.conItem.eq(defaultIndex).css("display","block");
			}else{
				if(effects == "slideX"){
					floatType = "left";
					posType = "left";
					sizeType = "width";
					size = elemWidth;
				}else if(effects == "slideY"){
					floatType = "none";
					posType = "top";
					sizeType = "height";
					size = elemHeight;
				}

				tab.content.css({
					"position": "relative",
					"width": elemWidth,
					"height": elemHeight,
					"overflow": "hidden"
				});

				distance = - (size * defaultIndex);

				tab.con.css(sizeType, size * tab.len)
					.css("position", "absolute")
					.css(posType, distance);

				tab.conItem.css("float", floatType);
			}

			tab.menuItem.eq(defaultIndex).addClass("current");
		};

		// 事件初始化
		var initAffair = function(){
			var trigger = tab.config.trigger,
				dataName = tab.config.dataName,
				effects = tab.config.effects,
				delay = tab.config.delay,
				hoverTimer;

			tab.menuItem.each(function(i){
				$(this).attr(dataName, i);
			});

			if(trigger == "click" || trigger == "focus"){
				tab.menu.on(trigger, tab.config.tabMenuItem, function(){
					var nextItem = $(this);
					patterns[effects](nextItem);
				});
			}else if(trigger == "mouseenter"){
				tab.menu.on("mouseenter", tab.config.tabMenuItem, function(){
					var nextItem = $(this);
					clearInterval(tab.timer);
					hoverTimer = setTimeout(function(){
						patterns[effects](nextItem);
					}, delay);
				});
				tab.menu.on("mouseleave", tab.config.tabMenuItem, function(){
					clearTimeout(hoverTimer);
					if (tab.config.auto) {
						tab.timer = setInterval(function(){
							patterns[effects]();
						},tab.config.interval);
					}
				});
			}

		};

		// 自动切换
		var autoTab = function(){
			var effects = tab.config.effects;
			tab.timer = setInterval(function(){
				patterns[effects]();
			},tab.config.interval);
		};

		var patterns = {
			// 默认切换效果
			normal: function(obj){
				var nextItem = obj,
					dataName = tab.config.dataName,
					currentItem = tab.menuItem.filter(".current"),
					currentIndex = parseInt(currentItem.attr(dataName)),
					currentConItem = tab.conItem.eq(currentIndex),
					nextIndex, nextConItem;

				// 判断是否传入下一个栏目
				if(nextItem){
					nextIndex = parseInt(nextItem.attr(dataName));
					nextConItem = tab.conItem.eq(nextIndex);
				}else{
					nextIndex = currentIndex == tab.len - 1 ? 0 : currentIndex + 1;
					nextItem = tab.menuItem.eq(nextIndex);
					nextConItem = tab.conItem.eq(nextIndex);
				}

				if(currentIndex != nextIndex){
					// 切换栏目当前状态
					currentItem.removeClass("current");
					nextItem.addClass("current");
					// 显示当前选中栏目的内容
					currentConItem.css("display","none");
					nextConItem.css("display","block");
				}
				return false;
			}
		};

		// 添加tab切换效果
		$.each({
			slideX: ["left", "width"],
			slideY: ["top", "height"] 
		}, function(name, val){
			var posName = val[0],
				sizeType = val[1];

			patterns[name] = function(obj){
				var nextItem = obj,
					dataName = tab.config.dataName,
					currentItem = tab.menuItem.filter(".current"),
					currentIndex = parseInt(currentItem.attr(dataName)),
					currentConItem = tab.conItem.eq(currentIndex),
					elemSize = parseInt(tab.conItem.css(sizeType)),
					thresholdVal = elemSize * tab.len,
					tabCon = tab.con,
					multiple = 1,
					animateData = {},
					duration = tab.config.duration,
					easing = tab.config.easing,
					nextIndex, nextConItem, distance,
					thresholdElem, distance,
					complete = function(){};

				// 判断是否传入下一个展示的栏目
				if(nextItem){
					nextIndex = nextItem.attr(dataName);
					multiple = nextIndex - currentIndex;
				}else{
					nextIndex = currentIndex == tab.len - 1 ? 0 : currentIndex + 1;
					nextItem = tab.menuItem.eq(nextIndex);
					// 当前处在栏目最后一项，自动切换时需把栏目内容第一项定位到最后
					// 自动切换完成时，再把定位恢复到默认状态
					if(currentIndex == tab.len - 1){
						thresholdElem = tab.conItem.eq(0);
						thresholdElem.css("position","absolute")
							.css(posName, thresholdVal);
						complete = function(){
							tabCon.css(posName,"0px");
							thresholdElem.css("position","")
								.css(posName,"");
						};
					}
				}

				distance = - (elemSize * multiple);
				
				// 即将改变的属性字面量，属性名不支持变量
				animateData[posName] = "+=" + distance;

				currentItem.removeClass("current");
				nextItem.addClass("current");

				// tab内容切换
				tabCon.stop(true, true)
					.animate(animateData, duration, easing, complete);
                
                //回调函数
                tab.config.callback();
			}

		});

		init(options);

	};

})(jQuery);