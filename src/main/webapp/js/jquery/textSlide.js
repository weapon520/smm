/*
 * author: Tracy
 * email: cuixigame@gmail.com
 * date: 2014.7.13
 * slideX(水平切换)、slideY(纵向切换)
 */


$.fn.mulSlide = function(options){

	// 默认参数
	var defaults = {
		effects				: "slideX",			// String 滚动效果
		delay				: 500,				// Number 滚动时间
		interval			: 5000,				// Number 滚动时间间隔
		multiple			: 1,				// Number 滚动视窗中显示元素个数
		easing				: "swing",			// String 缓动效果
		enterStop			: false				// Boolean 鼠标划上是否悬停
	};

	if(this.length > 1){
		this.each(function(){
			$(this).mulSlide(options);
		});
	}else if(this.length == 0){
		return this;
	}

	var mulSlide = this;
	mulSlide.config = {};
	mulSlide.count = 0;
	mulSlide.patterns = {};
	mulSlide.interval = null;
	mulSlide.slideList = mulSlide.find(".mulSlide");
	mulSlide.slideLi = mulSlide.slideList.find(".mulSlide_item");
	mulSlide.len = mulSlide.slideLi.length;
	mulSlide.width = mulSlide.slideLi.width();
	mulSlide.height = mulSlide.slideLi.height();
	mulSlide.thresholdElem = [];

	// 初始化函数
	var init = function(options){
		// 合并参数
		mulSlide.config = $.extend({}, defaults, options);

		// 初始化样式
		initLayout();

		// 自动轮播
		autoSlide();

		// 鼠标悬停
		if(mulSlide.config.enterStop){
			mulSlide.slideList.on("mouseenter", ".mulSlide_item", function(){
				clearInterval(mulSlide.interval);
			});
			mulSlide.slideList.on("mouseleave", ".mulSlide_item", function(){
				autoSlide();
			});
		}

	};

	// 样式初始化
	var initLayout = function(){
		var size, sizeType, floatType,
			effects = mulSlide.config.effects,
			multiple = mulSlide.config.multiple,
			elemWidth = mulSlide.width,
			elemHeight = mulSlide.height,
			mulWidth = elemWidth,
			mulHeight = elemHeight;

		if( effects == "slideX"){
			sizeType = "width";
			floatType = "left";
			mulWidth = multiple * elemWidth;
			size = elemWidth * mulSlide.len;
		}else{
			sizeType = "height";
			floatType = "none";
			mulHeight = multiple * elemHeight;
			size = elemHeight * mulSlide.len;
		}

		mulSlide.css({
			position: "relative",
			overflow: "hidden",
			width: mulWidth,
			height: mulHeight
		});

		mulSlide.slideList.css("position", "absolute")
			.css(sizeType, size);

		mulSlide.slideLi.css("float", floatType);

	};

	// 自动轮播
	var autoSlide = function(){
		var effects = mulSlide.config.effects;
		mulSlide.interval = setInterval(function(){
			mulSlide.patterns[effects]();
		}, mulSlide.config.interval);
	};

	// 轮播效果
	$.each({
		slideX: ["left", "width"],
		slideY: ["top", "height"]
	}, function(name, val){
		var posName = val[0],
			sizeType = val[1];

		mulSlide.patterns[name] = function(){
			var mulData = {},
				isForward = mulSlide.config.isForward,
				multiple = mulSlide.config.multiple,
				elemSize = parseInt(mulSlide.slideLi.css(sizeType)),
				thresholdVal = mulSlide.len * elemSize,
				delay = mulSlide.config.delay,
				easing = mulSlide.config.easing,
				complete = function(){},
				thresholdIndex, distance;

			if(mulSlide.count < mulSlide.len - multiple){
				mulSlide.count++;
			}else{
				thresholdIndex = mulSlide.count - (mulSlide.len - multiple);
				mulSlide.thresholdElem[thresholdIndex] = mulSlide.slideLi.eq(thresholdIndex);
				mulSlide.thresholdElem[thresholdIndex].css("position", "relative")
					.css(posName, thresholdVal);

				if( mulSlide.count == mulSlide.len - 1 ){
					complete = function(){
						mulSlide.slideList.css(posName, 0);
						$.each(mulSlide.thresholdElem, function(i, elem){
							elem.css("position", "")
								.css(posName, "");
						});
					};
					mulSlide.count = 0;
				}else{
					mulSlide.count++;
				}
			}

			distance = elemSize;
			mulData[posName] = "-=" + distance;

			mulSlide.slideList
				.stop(true, true)
				.animate(mulData, delay, easing, complete);
		
		};

	});

	// 初始化
	init(options);

	return this;
};
