$(function(){
	// banner轮播图
	$('.banner').slider({
		effects: 'slideX',
		fullScreen: false
	});

	// 合作银行滚动
    $(".picture").mulSlide({
        effects: "slideX",
        multiple: 6,
        interval: 2500
    });

    // 合作伙伴滚动
    $('.store_cop_module').slider({
    	effects: 'slideX',
    	fullScreen: false,
    	hasControl: false
    });

    // 仓储布局
    $('.store_plan').hover(function(){
    	$(this).stop(true, false).animate({
    		left: 0
    	}, 500);
    }, function(){
    	$(this).stop(true, false).animate({
    		left: '-840px'
    	}, 500);
    });

    // 服务内容
    $('.service_main_detail').hover(function(){
    	$(this).stop(true, false).animate({
    		top: 0
    	}, 500);
    }, function(){
    	$(this).stop(true, false).animate({
    		top: '-480px'
    	}, 500);
    });

    // 物流服务
    $('.wuliu_intro').hover(function(){
    	$(this).stop(true, false).animate({
    		top: '-240px'
    	}, 500);
    }, function(){
    	$(this).stop(true, false).animate({
    		top: 0
    	}, 500);
    });







});