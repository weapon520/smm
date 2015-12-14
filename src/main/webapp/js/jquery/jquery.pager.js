/*
* jQuery pager plugin
* Version 1.0 (12/22/2008)
* @requires jQuery v1.2.6 or later
*
* Example at: http://jonpauldavies.github.com/JQuery/Pager/PagerDemo.html
*
* Copyright (c) 2008-2009 Jon Paul Davies
* Dual licensed under the MIT and GPL licenses:
* http://www.opensource.org/licenses/mit-license.php
* http://www.gnu.org/licenses/gpl.html
* 
* Read the related blog post and contact the author at http://www.j-dee.com/2008/12/22/jquery-pager-plugin/
*
* This version is far from perfect and doesn't manage it's own state, therefore contributions are more than welcome!
*
* Usage: .pager({ pagenumber: 1, pagecount: 15, buttonClickCallback: PagerClickTest });
*
* Where pagenumber is the visible page number
*       pagecount is the total number of pages to display
*       buttonClickCallback is the method to fire when a pager button is clicked.
*
* buttonClickCallback signiture is PagerClickTest = function(pageclickednumber) 
* Where pageclickednumber is the number of the page clicked in the control.
*
* The included Pager.CSS file is a dependancy but can obviously tweaked to your wishes
* Tested in IE6 IE7 Firefox & Safari. Any browser strangeness, please report.
*/
(function($) {

    $.fn.pager = function(options) {

        var opts = $.extend({}, $.fn.pager.defaults, options);

        return this.each(function() {
        $(this).empty();
        //2页以上才显示分页条
        if(parseInt(opts.pagecount)<2){
            return;
        }
        // empty out the destination element and then render out the pager with the supplied opts
            $(this).append(gotoFn(parseInt(opts.pagenumber),parseInt(opts.pagecount),opts.hasGoto,opts.buttonClickCallback));
            $(this).append(renderpager(parseInt(opts.pagenumber), parseInt(opts.pagecount), parseInt(opts.pagesize), opts.buttonClickCallback));
            // specify correct cursor activity
            $('.pages li').mouseover(function() { document.body.style.cursor = "pointer"; }).mouseout(function() { document.body.style.cursor = "auto"; });
        });
    };

    // render and return the pager with the supplied options
    function renderpager(pagenumber, pagecount, pagesize, buttonClickCallback) {

        // setup $pager to hold render
        var $pager = $('<ul class="pages clearfix"></ul>');

        // add in the previous and next buttons
        $pager.append(renderButton('上一页', pagenumber, pagecount, buttonClickCallback));

        // pager currently only handles 10 viewable pages ( could be easily parameterized, maybe in next version ) so handle edge cases
        var startPoint = 1;
        var endPoint = pagesize;

        if (pagenumber > Math.floor(pagesize/2)) {
            if(pagenumber > pagecount-Math.floor(pagesize/2)){
                startPoint = pagecount - pagesize + 1;
                endPoint = pagecount;
            }else{
                startPoint = pagenumber - Math.floor(pagesize/2);
                endPoint = parseInt(pagenumber) + Math.floor(pagesize/2);
            }
        }
        if (endPoint > pagecount) {
            startPoint = pagecount - pagesize - 1;
            endPoint = pagecount;
        }

        if (startPoint < 1) {
            startPoint = 1;
        }
        if(startPoint>1){
            var startButton = $('<li class="page-number">1</li>');
            $pager.append(startButton).append('<span class="page-break">...</span>');
            startButton.click(function() { buttonClickCallback(1); });
        }
        // loop thru visible pages and render buttons
        for (var page = startPoint; page <= endPoint; page++) {

            var currentButton = $('<li class="page-number">' + (page) + '</li>');

            page == pagenumber ? currentButton.addClass('pgCurrent') : currentButton.click(function() { buttonClickCallback(this.firstChild.data); });
            currentButton.appendTo($pager);
        }

        // render in the next and last buttons before returning the whole rendered control back.
        if(endPoint<pagecount){
            var endButton = $('<li class="page-number">' + pagecount + '</li>');
            $pager.append('<span class="page-break">...</span>').append(endButton);
            endButton.click(function() { buttonClickCallback(this.firstChild.data); });
        }
        $pager.append(renderButton('下一页', pagenumber, pagecount, buttonClickCallback));

        return $pager;
    }
    function gotoFn(pagenumber,pagecount,hasGoto,buttonClickCallback){
        var $pagerGoto=$('<div class="pagesGoto"></div>');
        var html='';
        if(pagecount>1 && hasGoto){
            //var gotoNumber=pagecount==pagenumber ? 1 : pagenumber + 1;
            html +='共'+pagecount+'页 转第<input type="text" id="pageToNumber" value="'+pagenumber+'" />页 <button type="button" id="pageGoto">确认</button>';
        }
        $pagerGoto.html(html);
        if(hasGoto){
            $pagerGoto.find("button").on("click",function(){
                var destPage=$pagerGoto.find("#pageToNumber").val();
                if(destPage==""){
                    layer.msg("跳转页数不能为空","info");
                    return false;
                }else if(isNaN(destPage)){
                    layer.msg("跳转页数不是数字","info");
                }else if(destPage>pagecount){
                    layer.msg("跳转页数不能大于总页数","info");
                    return false;
                }else{
                    buttonClickCallback(destPage);
                }
            });
        }
        return $pagerGoto;
    }

    // renders and returns a 'specialized' button, ie 'next', 'previous' etc. rather than a page number button
    function renderButton(buttonLabel, pagenumber, pagecount, buttonClickCallback) {

        var $Button = $('<li class="pgNext">' + buttonLabel + '</li>');

        var destPage = 1;

        // work out destination page for required button type
        switch (buttonLabel) {
            case "首页":
                destPage = 1;
                break;
            case "上一页":
                destPage = pagenumber - 1;
                break;
            case "下一页":
                destPage = pagenumber + 1;
                break;
            case "尾页":
                destPage = pagecount;
                break;
        }

        // disable and 'grey' out buttons if not needed.
        if (buttonLabel == "首页" || buttonLabel == "上一页") {
            pagenumber <= 1 ? $Button.addClass('pgEmpty') : $Button.click(function() { buttonClickCallback(destPage); });
        }
        else {
            pagenumber >= pagecount ? $Button.addClass('pgEmpty') : $Button.click(function() { buttonClickCallback(destPage); });
        }

        return $Button;
    }

    // pager defaults. hardly worth bothering with in this case but used as placeholder for expansion in the next version
    $.fn.pager.defaults = {
        pagenumber: 1,
        pagecount: 1,
        pagesize:9,
        hasGoto:true
    };

})(jQuery);





