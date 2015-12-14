$(function(){
	$(".case").slider({
		event: 'mouseenter',
		effects: "slideX",
		fullScreen: false,
		interval: 30000,
		createControl: false,
		hasSideControl: true,
		hasState:false,
		fadeSideControl: false,
	});

	// 融资
	/*$('.tabs').tab({
	 defaultIndex:type,
	 trigger: 'click',
	 });
	 */

	//新的切换
	$(".tab_con,.suc_wrap").children("li").css({"opacity":0,"display":"none"});

/*	var target1= $("#typeId").val();
	if(target1!=""){
		/!*$(".tab_con,.suc_wrap").find("li:eq("+index+")").css("opacity",1);*!/

		$(".tab_menu li").eq(target1).addClass("current");
		$(".tab_con").children("li").eq(target1).css({"opacity":1,"display":"block"});
		$(".suc_wrap").children("li").eq(target1).css({"opacity":1,"display":"block","color":"#333"});

	}else {
		$(".tab_menu li:eq(0)").addClass("current");
		$(".tab_con").children("li:eq(0)").css({"opacity":1,"display":"block","color":"#000"});
		$(".suc_wrap").children("li:eq(0)").css({"opacity":1,"display":"block","color":"#000"});

	}*/

	/*初始化tab选中*/
	var current = $(".tab_menu .current").index();
	$(".tab_con").children("li").eq(current).css({"opacity":1,"display":"block"});
	$(".suc_wrap").children("li").eq(current).css({"opacity":1,"display":"block"});

	//绑定tab切换成功案例的事件
	$(".tab_menu").on("click","li",function(){
		var target2= $(this).index();
		$(this).siblings().removeClass("current");
		$(this).addClass("current");

		$(".tab_con").children("li").css({"opacity":0,"display":"none"});
		$(".suc_wrap").children("li").css({"opacity":0,"display":"none"});
		$(".tab_con").children("li").eq(target2).css({"opacity":1,"display":"block"});
		$(".suc_wrap").children("li").eq(target2).css({"opacity":1,"display":"block"});
	});

});