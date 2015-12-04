<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/inc/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
<title>出错了 - </title>
	<link rel="stylesheet" href="${applicationScope.imagesDomain}/css/front/error.css" />
</head>
<body class="otherBg">
<!-- %@ include file="/inc/simpleHeader.jsp"% > -->

<div class="maxWidth">
	<div class="wrapper clearfix">
		<div class="left">
			<img  src="/images/front/png_500.png"/>
		</div>
		<div class="right">
			<h1>对不起，没有找到您所请求的页面</h1>
			<p>您要查看的网页可能已被删除、名称已被更改，或者暂时不可用。</p>
			<p><span class="time">10</span>秒后自动返回到网站首页，您还可以给我们反馈</p>
			<div class="btn_wrap">
				<a class="btn1" href="http://p.qiao.baidu.com//im/index?siteid=7407813&ucid=11147196" target="_blank"><span></span>给我们反馈</a>
				<a class="btn2" href="/"><span></span>返回首页</a>
			</div>
		</div>
	</div>
</div>
<!-- 公用底部 -->
<script type="text/javascript" src="${applicationScope.imagesDomain}/js/jquery/jquery-1.9.1.min.js"></script>
<script type="text/javascript">

	$(function(){
		var time= 10;
		var obj = $(".time")
		var interval1=  setInterval(function(){
			obj.html(time);
			time--;
		},1000);
		setTimeout(function(){
			clearInterval(interval1);
			location.href="/";
		},time*1000);
	})
</script>
</body>
</html>