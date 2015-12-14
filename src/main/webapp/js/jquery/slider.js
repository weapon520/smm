/*
 * versions: 1.03
 * author: Tracy
 * email: cuixigame@gmail.com
 * date: 2014.7.13
 * update: 2015.1.4
 * 切换方向可设置为fade(渐隐)、slideX(水平切换)、slideY(纵向切换)
 * 触发轮播事件event可设置'click'、'mouseenter'
 */

 'use strict'

 ;(function($){
 	$.fn.slider = function(options){
 		//默认参数
 		var defaults = {
 			event			: 'click',			// String 轮播触发切换事件类型
 			auto        	: true,				// Boolean 是否自动轮播
 			effects			: 'fade',			// String 切换效果
 			interval		: 5000,				// Number 自动轮播时间间隔
 			slideDuration	: 600,				// Number 轮播图运动时间
 			defaultIndex	: 0,				// Number 默认展示项
 			isForward		: true,				// Boolean 轮播的方向
 			fullScreen		: false,			// Boolean 是否为全屏轮播
 			hasControl		: true,				// Boolean 是否存在轮播控制器
			createControl   : true,				// Boolean 是否创建轮播控制器
 			hasSideControl	: false,			// Boolean 是否存在左右控制器
			fadeSideControl	: true,				// Boolean 左右控制器显示方式
			hasState		: false,			// Boolean 轮播状态显示索引数字
 			callback		:function(){}		// Fuction 回调函数
 		};
		
 		if(this.length === 0){
 			return this;
 		}else if(this.length > 1){
 			this.each(function(){
 				$(this).slider(options);
 			});
 			return this;
 		}
		
 		var slider = this;

 		//init
 		var init = function(options){
 			//参数
 			slider.config = $.extend({}, defaults, options);
	 		slider.interval = null;
	 		slider.timer = null;
	 		slider.pattern = [];
	 		slider.setp = false;
	 		slider.direction = true;
	 		slider.nextId = 0;
 			slider.currentId = slider.config.defaultIndex;
 			slider.direction = slider.config.isForward;
	 		slider.slideBox = slider.find('.sliderBox');				//轮播图区域
	 		slider.slideList = slider.find('.slider');					//轮播图列表
	 		slider.slideElem = slider.find('.slider li');				//轮播图
 			slider.count = slider.slideElem.length;
 			//启动轮播
 			setup();
 		};

 		//setup
 		var setup = function(){
 			// 样式初始化
 			if(slider.config.effects === 'fade'){
 				sliderAffair.fadeLayout();
 			} else {
 				sliderAffair.scrollLayout();
 			}
			// 创建轮播控制器
 			if (slider.config.hasControl) {
				if(slider.config.createControl){
 					sliderAffair.createControl();
				}
 				slider.slideControl = slider.find('.control li');
	 			slider.slideControl
	 				.eq(slider.config.defaultIndex)
	 				.addClass('current');
 				// 给轮播控制器绑定事件
	 			slider.slideControl.each(function(i){
		 			$(this).on(slider.config.event, function(){
		 				if(!!slider.timer){ clearTimeout(slider.timer); }
		 				slider.timer = setTimeout(function(){
			 				clearInterval(slider.interval);
			 				slideShow(i);
				 			if(slider.config.auto && slider.count > 1){
				 				autoSlider(null);
				 			}
							if(slider.config.hasState){
								slider.find('.index').html(i+1);
							}
		 				}, 200);
						slider.config.callback(slider);
		 			});
	 			});
 			}
			// 创建左右控制器
 			if (slider.config.hasSideControl) {
 				sliderAffair.createSideControl();
 				slider.sideControl = slider.find('.sliderSwitch');
 				slider.slidePreview = slider.find('.preview');
 				slider.slideNext = slider.find('.next');
				// 左右箭头显示方式
				if(slider.config.fadeSideControl){
					slider.sideControl.css('display', 'none');
					slider.hover(function(){
						slider.sideControl.css('display', 'block');
					}, function(){
						slider.sideControl.css('display', 'none');
					});
				}
 				// 显示上一张幻灯片
	 			slider.slidePreview.on('click', function(){
	 				var cIndex;
	 				slider.setp = true;
	 				slider.direction = false;
	 				clearInterval(slider.interval);
	 				if(slider.currentId === 0){
	 					cIndex = slider.count - 1;
	 				}else{
	 					cIndex = slider.currentId - 1;
	 				}
	 				
	 				slideShow(cIndex);
		 			if(slider.config.auto && slider.count > 1){
		 				autoSlider(null);
		 			}
		 			slider.setp = false;
		 			slider.direction = slider.config.isForward;
					slider.config.callback(slider);
					if(slider.config.hasState){
						slider.find('.index').html(cIndex+1);
					}
		 			return false;
	 			});
	 			// 显示下一张幻灯片
	 			slider.slideNext.on('click', function(){
	 				var cIndex;
	 				slider.setp = true;
	 				slider.direction = true;
	 				clearInterval(slider.interval);
	 				if(slider.currentId === slider.count - 1){
	 					cIndex = 0;
	 				}else{
	 					cIndex = slider.currentId + 1;
	 				}
	 				slideShow(cIndex);
		 			if(slider.config.auto && slider.count > 1){
		 				autoSlider(null);
		 			}
		 			slider.setp = false;
		 			slider.direction = slider.config.isForward;
					slider.config.callback(slider);
					if(slider.config.hasState){
						slider.find('.index').html(cIndex+1);
					}
		 			return false;
	 			});
 			}
			//创建轮播状态栏
			if (slider.config.hasState){
				sliderAffair.createState();
				slider.find('.index').html('1');
				slider.find('.index_max').html('/'+slider.slideControl.length);
			}
 			//自动轮播
 			if(slider.config.auto && slider.count > 1){
 				autoSlider(null);
 			}
 			// resize时重置全屏轮播宽度
 			if(slider.config.fullScreen){
 				var timer;
	 			$(window).on('resize', function(){
	 				if( !!timer ){ clearTimeout(timer); }
	 				timer = setTimeout(sliderAffair.resizeWidth, 100);
	 			});
 				// 全屏轮播图设置为背景图
 				sliderAffair.setBackgroundImage();
 			}
 		};

 		//slideShow
 		var slideShow = function(nextId){
			if(slider.currentId === nextId){return;}
 			var run = sliderAffair.patterns[slider.config.effects];
 			run(nextId);
 		};

 		//autoSlider
 		var autoSlider = function(nextId){
			slider.interval = setInterval(function(){
				slideShow(nextId);
				if(slider.config.hasState){
					slider.find('.index').html(slider.currentId+1);
				}
				slider.config.callback(slider);
			}, slider.config.interval);
 		};

 		//sliderAffair
 		var sliderAffair = {
 			// 渐隐样式初始化
 			fadeLayout : function(){
 				if(slider.config.fullScreen){
 					var width = document.body.clientWidth,
					height = parseFloat(slider.slideElem.outerHeight());
 					slider.slideElem.css('width', width);
 					slider.slideBox.css('width', width);
					slider.slideBox.css('height', height);
 				}
 				slider.slideBox.css('position', 'relative');
	 			slider.slideElem.css({
	 					'position' : 'absolute',
	 					'left'	   : '0px',
	 					'top'	   : '0px',	
	 					'opacity'  : 0
	 				})
	 				.eq(slider.currentId)
	 				.css({
	 					'opacity' : 1
	 				});
 			},
 			// 滚动样式初始化
 			scrollLayout : function(){
 				var width = parseFloat(slider.slideElem.outerWidth()),
 					height = parseFloat(slider.slideElem.outerHeight());
 				if(slider.config.fullScreen){
 					width = document.body.clientWidth;
 					slider.slideElem.css('width', width);
 					slider.slideBox.css('width', width);
 					slider.slideList.css('width', width*slider.count);
 				}
 				if(slider.config.effects === 'slideX'){
 					slider.slideList.css({
 							'position' : 'absolute',
 							'left'	   : '-' + slider.currentId * width,
 							'width'	   : slider.count * width,
 							'height'   : height
 						});
 					slider.slideElem.css('float', 'left');
 				}else if(slider.config.effects === 'slideY'){
 					slider.slideList.css({
 							'position' : 'absolute',
 							'top'	   : '-' + slider.currentId * height,
 							'height'   : slider.count * height,
 							'width'    : width
 						});
 				}
 				slider.slideBox.css({
 						'position' : 'relative',
 						'width'	   : width,
 						'height'   : height,
 						'overflow' : 'hidden'
 					});
 			},
 			// patterns
 			patterns : {
 				'fade' : function(nextId){
 					if(nextId === null){
 						slider.nextId = slider.currentId === slider.count-1 ? 0 : slider.currentId + 1;
 					}else{
 						slider.nextId = nextId;
 					}
					slider.slideElem
			 			.eq(slider.currentId)
			 			.stop(true, true)
			 			.animate({
			 				opacity: 0,
			 				zIndex: 0
			 			}, slider.config.slideDuration);

					slider.slideElem
						.eq(slider.nextId)
						.stop(true, true)
						.animate({
							opacity: 1,
							zIndex: 1
						}, slider.config.slideDuration);

		 			slider.slideControl
			 			.eq(slider.currentId)
			 			.removeClass('current');

		 			slider.slideControl
			 			.eq(slider.nextId)
			 			.addClass('current');

		 			slider.currentId = slider.nextId;
 				}
 			},
 			// 重置轮播图宽度
 			resizeWidth : function(){
 				var width = Math.max(document.body.clientWidth, parseInt(slider.slideBox.css('minWidth'))),
 					left = -width*slider.currentId;
		 		slider.slideBox.css('width', width);
		 		slider.slideList.css('width', width*slider.count);
		 		if(slider.config.effects !== 'fade'){
		 			slider.slideList.css('left', left);
		 		}
		 		slider.slideElem.css('width', width);
 			},
 			// 生成轮播控制器
 			createControl : function(){
 				var controlUL = document.createElement('ul');
 				controlUL.className = 'control';
				if(slider.count>1){
					//增加判断，当轮播图大于一张时才显示控制器只有一张不创建控制器。edit by *建海 *2015/11/9
					for(var i=0; i<slider.count; i++){
						var controlLI = document.createElement('li');
						controlLI.innerHTML = i + 1;
						controlUL.appendChild(controlLI);
					}
					slider.get(0).appendChild(controlUL);

				}
 				return controlUL;
 			},
 			// 生成左右控制器
 			createSideControl : function(){
 				var sideControl = document.createElement('div'),
 					preview = document.createElement('span'),
 					next = document.createElement('span');

 				sideControl.className = 'sliderSwitch';
 				preview.className = 'preview';
 				next.className = 'next';

 				sideControl.appendChild(preview);
 				sideControl.appendChild(next);
				slider.get(0).appendChild(sideControl);
				return sideControl;
 			},
 			// 全屏轮播时，把图片作为背景显示
 			setBackgroundImage : function(){
 				for(var i=0; i<slider.count; i++){
 					var slideLink = slider.slideElem.eq(i).find('a'),
 						slideSrc = slideLink.attr('data-src');
					if(slideSrc){
						slideLink.css({
							display: 'block',
							backgroundImage: 'url(' + slideSrc +')',
							backgroundPosition: 'center'
						});
					}
 				}
 			},
			createState: function(){
				var state=document.createElement('span'),
					current=document.createElement('span'),
					num=document.createElement('span');
				state.className='state';
				current.className='index';
				num.className='index_max';
				state.appendChild(current);
				state.appendChild(num);
				slider.get(0).appendChild(state);
			}
 		};

 		//上下切换和左右切换
 		$.each({
 			slideX : ['width', 'left'],
 			slideY : ['height', 'top']
 		}, function(name, val){
 			sliderAffair.patterns[name] = function(nextId){
	 			var thresholdElem,
	 				thresholdVal,
	 				distance,
	 				data = {},
	 				sizeType = val[0],
	 				posName = val[1],
	 				sliderLi = parseFloat(slider.slideElem.css(sizeType)),
					isForward = slider.direction,
					complete = function(){};

				if(nextId === null){
					distance = isForward ? '-' + sliderLi : sliderLi;
					slider.nextId = slider.currentId === slider.count - 1 ? 0 : slider.currentId + 1;
				}else if(slider.setp){
					distance = isForward ? '-' + sliderLi : sliderLi;
					slider.nextId = nextId;
				}else{
					distance = (slider.currentId - nextId) * sliderLi;
					isForward = nextId > slider.currentId ? true : false;
					slider.nextId = nextId;
				}

 				if(isForward){
 					if(slider.currentId === slider.count - 1){
 						thresholdElem = slider.slideElem.eq(0);
 						thresholdVal = slider.count * sliderLi + 'px';
	 					complete = function(){
	 						slider.slideList.css(posName, '0px');
	 						thresholdElem.css('position', '').css(posName, '');
	 					};	
 					}
 				}else {
 					if (slider.currentId === 0) {
 						thresholdElem = slider.slideElem.eq(slider.count - 1);
 						thresholdVal = '-' + slider.count * sliderLi + 'px';
	 					complete = function(){
	 						slider.slideList.css(posName, '-' + (slider.count - 1) * sliderLi + 'px');
	 						thresholdElem.css('position', '').css(posName, '');
	 					};
 					}
 				}
 				if(thresholdElem){
 					thresholdElem.css('position', 'relative')
 						.css(posName, thresholdVal);
 				}
 				// 轮播图控制器样式切换
 				if(slider.config.hasControl){
	 				slider.slideControl
	 					.eq(slider.currentId)
	 					.removeClass('current');

	 				slider.slideControl
	 					.eq(slider.nextId)
	 					.addClass('current');
 				}

				data[posName] = '+=' + distance;

				// 轮播图切换
 				slider.slideList
 					.stop(true, true)
 					.animate(data, slider.config.slideDuration, complete);

 				slider.currentId = slider.nextId;
 			};
 		});

 		//初始化
 		init(options);

 		return this;
 	};
 })(jQuery);