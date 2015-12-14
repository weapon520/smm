$(function(){

	// 内页导航
	$('.nav_inner_list li').each(function(index){
		var itemWidth = $(this).innerWidth(),
			offsetLeft = $(this).position().left;

		if(index !== 0){
			var subMenu = $('.subnav_list li').eq(index-1),
				subMenuWidth = subMenu.innerWidth(),
				left = offsetLeft + itemWidth/2 - subMenuWidth/2;

			
			left = left < 0 ? 0 : left;
			left = left + subMenuWidth > 1000 ? 1000 - subMenuWidth : left;

			subMenu.css('left', left);
		}		
	});

	$('.nav_inner_list').on('mouseenter', 'li', function(){
		var index = $(this).index();

		$('.nav_inner_list li').removeClass('current');
		$(this).addClass('current');
		$('.subnav_list li').removeClass('current');

		if(index !== 0){
			var subMenu = $('.subnav_list li').eq(index-1);
			subMenu.addClass('current');
		}

	});

});


