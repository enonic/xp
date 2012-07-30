$(document).ready(function(){ 

	// Media plugin setup
	$.fn.media.defaults.caption = false;
	$.fn.media.defaults.params.wmode = 'transparent';
	$(".media").media();

	$("ul.sf-menu").supersubs({
		minWidth: 15,
		maxWidth: 25,
		extraWidth: 1	
	}).superfish({ 
    	delay:     500               //  delay on mouseout 
    }).find('ul').bgIframe({opacity:false});
	
	// Toggle subitems
	if ($("#articlelinks").length > 0 && $("#framework").height() > $(window).height()) {
		$("#articlelinks").prepend("<p id='toggler'><a href='javascript:void(0);'>Vis/skjul undersider</a></p>");
		$("#toggler").css('float', 'right').bind("click", function(){
			$(".columns li").toggle();
		});
	}
	
	 
	// App popupscript to form links
	//$("a.popup").bind("click", function(){
	//	return openWin(this.href,'Gjensidige', 640, 800);
	//});
	
}); 

var newwindow = '';
function openWin(url,title,bredde,hoyde){

	this.url = url;
	this.title = title;
	var height = screen.availHeight;
	var width = screen.availWidth;
	var left = (width/2) - 400;
	var top = 10;
 	
	if (!newwindow.closed && newwindow.location) {
		newwindow.location.href = url;
	} else {
		newwindow=window.open(url,title,'menubar=1,status=1,location=1,toolbar=1,scrollbars=1,resizable=1,height='+hoyde+',width='+bredde+',left='+left+',top='+top+'');
		if (!newwindow.opener) newwindow.opener = self;
	}	
	if (window.focus) {
		newwindow.focus()
	}
	 return false;
}

function createCookie(name,value,days) {
  if (days) {
    var date = new Date();
    date.setTime(date.getTime()+(days*24*60*60*1000));
    var expires = "; expires="+date.toGMTString();
  } else var expires = "";
    document.cookie = name+"="+value+expires+"; path=/";
}

function readCookie(name) {
  var nameEQ = name + "=";
  var ca = document.cookie.split(';');
  for(var i=0;i < ca.length;i++) {
    var c = ca[i];
    while (c.charAt(0)==' ') c = c.substring(1,c.length);
    if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
  }
  return null;
}

function eraseCookie(name) {
  createCookie(name,"",-1);
}


/*
 * Superfish v1.4.8 - jQuery menu widget
 * Copyright (c) 2008 Joel Birch
 *
 * Dual licensed under the MIT and GPL licenses:
 * 	http://www.opensource.org/licenses/mit-license.php
 * 	http://www.gnu.org/licenses/gpl.html
 *
 * CHANGELOG: http://users.tpg.com.au/j_birch/plugins/superfish/changelog.txt
 */

(function($){
	$.fn.superfish = function(op){

		var sf = $.fn.superfish,
			c = sf.c,
			$arrow = $(['<span class="',c.arrowClass,'"> &#187;</span>'].join('')),
			over = function(){
				var $$ = $(this), menu = getMenu($$);
				clearTimeout(menu.sfTimer);
				$$.showSuperfishUl().siblings().hideSuperfishUl();
			},
			out = function(){
				var $$ = $(this), menu = getMenu($$), o = sf.op;
				clearTimeout(menu.sfTimer);
				menu.sfTimer=setTimeout(function(){
					o.retainPath=($.inArray($$[0],o.$path)>-1);
					$$.hideSuperfishUl();
					if (o.$path.length && $$.parents(['li.',o.hoverClass].join('')).length<1){over.call(o.$path);}
				},o.delay);	
			},
			getMenu = function($menu){
				var menu = $menu.parents(['ul.',c.menuClass,':first'].join(''))[0];
				sf.op = sf.o[menu.serial];
				return menu;
			},
			addArrow = function($a){ $a.addClass(c.anchorClass).append($arrow.clone()); };
			
		return this.each(function() {
			var s = this.serial = sf.o.length;
			var o = $.extend({},sf.defaults,op);
			o.$path = $('li.'+o.pathClass,this).slice(0,o.pathLevels).each(function(){
				$(this).addClass([o.hoverClass,c.bcClass].join(' '))
					.filter('li:has(ul)').removeClass(o.pathClass);
			});
			sf.o[s] = sf.op = o;
			
			$('li:has(ul)',this)[($.fn.hoverIntent && !o.disableHI) ? 'hoverIntent' : 'hover'](over,out).each(function() {
				if (o.autoArrows) addArrow( $('>a:first-child',this) );
			})
			.not('.'+c.bcClass)
				.hideSuperfishUl();
			
			var $a = $('a',this);
			$a.each(function(i){
				var $li = $a.eq(i).parents('li');
				$a.eq(i).focus(function(){over.call($li);}).blur(function(){out.call($li);});
			});
			o.onInit.call(this);
			
		}).each(function() {
			var menuClasses = [c.menuClass];
			if (sf.op.dropShadows  && !($.browser.msie && $.browser.version < 7)) menuClasses.push(c.shadowClass);
			$(this).addClass(menuClasses.join(' '));
		});
	};

	var sf = $.fn.superfish;
	sf.o = [];
	sf.op = {};
	sf.IE7fix = function(){
		var o = sf.op;
		if ($.browser.msie && $.browser.version > 6 && o.dropShadows && o.animation.opacity!=undefined)
			this.toggleClass(sf.c.shadowClass+'-off');
		};
	sf.c = {
		bcClass     : 'sf-breadcrumb',
		menuClass   : 'sf-js-enabled',
		anchorClass : 'sf-with-ul',
		arrowClass  : 'sf-sub-indicator',
		shadowClass : 'sf-shadow'
	};
	sf.defaults = {
		hoverClass	: 'sfHover',
		pathClass	: 'overideThisToUse',
		pathLevels	: 1,
		delay		: 800,
		animation	: {opacity:'show'},
		speed		: 'normal',
		autoArrows	: true,
		dropShadows : true,
		disableHI	: false,		// true disables hoverIntent detection
		onInit		: function(){}, // callback functions
		onBeforeShow: function(){},
		onShow		: function(){},
		onHide		: function(){}
	};
	$.fn.extend({
		hideSuperfishUl : function(){
			var o = sf.op,
				not = (o.retainPath===true) ? o.$path : '';
			o.retainPath = false;
			var $ul = $(['li.',o.hoverClass].join(''),this).add(this).not(not).removeClass(o.hoverClass)
					.find('>ul').hide().css('visibility','hidden');
			o.onHide.call($ul);
			return this;
		},
		showSuperfishUl : function(){
			var o = sf.op,
				sh = sf.c.shadowClass+'-off',
				$ul = this.addClass(o.hoverClass)
					.find('>ul:hidden').css('visibility','visible');
			sf.IE7fix.call($ul);
			o.onBeforeShow.call($ul);
			$ul.animate(o.animation,o.speed,function(){ sf.IE7fix.call($ul); o.onShow.call($ul); });
			return this;
		}
	});

})(jQuery);

/*
 * Supersubs v0.2b - jQuery plugin
 * Copyright (c) 2008 Joel Birch
 *
 * Dual licensed under the MIT and GPL licenses:
 * 	http://www.opensource.org/licenses/mit-license.php
 * 	http://www.gnu.org/licenses/gpl.html
 *
 *
 * This plugin automatically adjusts submenu widths of suckerfish-style menus to that of
 * their longest list item children. If you use this, please expect bugs and report them
 * to the jQuery Google Group with the word 'Superfish' in the subject line.
 *
 */

;(function($){ // $ will refer to jQuery within this closure

	$.fn.supersubs = function(options){
		var opts = $.extend({}, $.fn.supersubs.defaults, options);
		// return original object to support chaining
		return this.each(function() {
			// cache selections
			var $$ = $(this);
			// support metadata
			var o = $.meta ? $.extend({}, opts, $$.data()) : opts;
			// get the font size of menu.
			// .css('fontSize') returns various results cross-browser, so measure an em dash instead
			var fontsize = $('<li id="menu-fontsize">&#8212;</li>').css({
				'padding' : 0,
				'position' : 'absolute',
				'top' : '-999em',
				'width' : 'auto'
			}).appendTo($$).width(); //clientWidth is faster, but was incorrect here
			// remove em dash
			$('#menu-fontsize').remove();
			// cache all ul elements
			$ULs = $$.find('ul');
			// loop through each ul in menu
			$ULs.each(function(i) {	
				// cache this ul
				var $ul = $ULs.eq(i);
				// get all (li) children of this ul
				var $LIs = $ul.children();
				// get all anchor grand-children
				var $As = $LIs.children('a');
				// force content to one line and save current float property
				var liFloat = $LIs.css('white-space','nowrap').css('float');
				// remove width restrictions and floats so elements remain vertically stacked
				var emWidth = $ul.add($LIs).add($As).css({
					'float' : 'none',
					'width'	: 'auto'
				})
				// this ul will now be shrink-wrapped to longest li due to position:absolute
				// so save its width as ems. Clientwidth is 2 times faster than .width() - thanks Dan Switzer
				.end().end()[0].clientWidth / fontsize;
				// add more width to ensure lines don't turn over at certain sizes in various browsers
				emWidth += o.extraWidth;
				// restrict to at least minWidth and at most maxWidth
				if (emWidth > o.maxWidth)		{ emWidth = o.maxWidth; }
				else if (emWidth < o.minWidth)	{ emWidth = o.minWidth; }
				emWidth += 'em';
				// set ul to width in ems
				$ul.css('width',emWidth);
				// restore li floats to avoid IE bugs
				// set li width to full width of this ul
				// revert white-space to normal
				$LIs.css({
					'float' : liFloat,
					'width' : '100%',
					'white-space' : 'normal'
				})
				// update offset position of descendant ul to reflect new width of parent
				.each(function(){
					var $childUl = $('>ul',this);
					var offsetDirection = $childUl.css('left')!==undefined ? 'left' : 'right';
					$childUl.css(offsetDirection,emWidth);
				});
			});
			
		});
	};
	// expose defaults
	$.fn.supersubs.defaults = {
		minWidth		: 9,		// requires em unit.
		maxWidth		: 25,		// requires em unit.
		extraWidth		: 0			// extra width can ensure lines don't sometimes turn over due to slight browser differences in how they round-off values
	};
	
})(jQuery);


/*
 * Metadata - jQuery plugin for parsing metadata from elements
 *
 * Copyright (c) 2006 John Resig, Yehuda Katz, Jorn Zaefferer, Paul McLanahan
 *
 * Dual licensed under the MIT and GPL licenses:
 *   http://www.opensource.org/licenses/mit-license.php
 *   http://www.gnu.org/licenses/gpl.html
 *
 * Revision: $Id: jquery.metadata.js 3620 2007-10-10 20:55:38Z pmclanahan $
 *
 */
(function($){$.extend({metadata:{defaults:{type:'class',name:'metadata',cre:/({.*})/,single:'metadata'},setType:function(type,name){this.defaults.type=type;this.defaults.name=name;},get:function(elem,opts){var settings=$.extend({},this.defaults,opts);if(!settings.single.length)settings.single='metadata';var data=$.data(elem,settings.single);if(data)return data;data="{}";if(settings.type=="class"){var m=settings.cre.exec(elem.className);if(m)data=m[1];}else if(settings.type=="elem"){if(!elem.getElementsByTagName)return;var e=elem.getElementsByTagName(settings.name);if(e.length)data=$.trim(e[0].innerHTML);}else if(elem.getAttribute!=undefined){var attr=elem.getAttribute(settings.name);if(attr)data=attr;}if(data.indexOf('{')<0)data="{"+data+"}";data=eval("("+data+")");$.data(elem,settings.single,data);return data;}}});$.fn.metadata=function(opts){return $.metadata.get(this[0],opts);};})(jQuery);

/*
 * jQuery Media Plugin for converting elements into rich media content.
 *
 * Examples and documentation at: http://malsup.com/jquery/media/
 * Copyright (c) 2007-2008 M. Alsup
 * Dual licensed under the MIT and GPL licenses:
 * http://www.opensource.org/licenses/mit-license.php
 * http://www.gnu.org/licenses/gpl.html
 *
 * @author: M. Alsup
 * @version: 0.82 (10/14/2008)
 * @requires jQuery v1.1.2 or later
 * $Id: jquery.media.js 2460 2007-07-23 02:53:15Z malsup $
 * Minified with YUI compressor
 */

(function(c){c.fn.media=function(m,l,n){return this.each(function(){if(typeof m=="function"){n=l;l=m;m={}}var w=g(this,m);if(typeof l=="function"){l(this,w)}var v=j();var p=v.exec(w.src)||[""];w.type?p[0]=w.type:p.shift();for(var u=0;u<p.length;u++){fn=p[u].toLowerCase();if(e(fn[0])){fn="fn"+fn}if(!c.fn.media[fn]){continue}var t=c.fn.media[fn+"_player"];if(!w.params){w.params={}}if(t){var s=t.autoplayAttr=="autostart";w.params[t.autoplayAttr||"autoplay"]=s?(w.autoplay?1:0):w.autoplay?true:false}var q=c.fn.media[fn](this,w);q.css("backgroundColor",w.bgColor).width(w.width);if(typeof n=="function"){n(this,q[0],w,t.name)}break}})};c.fn.media.mapFormat=function(m,l){if(!m||!l||!c.fn.media.defaults.players[l]){return}m=m.toLowerCase();if(e(m[0])){m="fn"+m}c.fn.media[m]=c.fn.media[l];c.fn.media[m+"_player"]=c.fn.media.defaults.players[l]};c.fn.media.defaults={width:400,height:400,autoplay:0,bgColor:"#ffffff",params:{wmode:"window"},attrs:{},flvKeyName:"file",flashvars:{},flashVersion:"7",expressInstaller:null,flvPlayer:"binary/30543/file",mp3Player:"binary/30543/file",silverlight:{inplaceInstallPrompt:"true",isWindowless:"true",framerate:"24",version:"0.9",onError:null,onLoad:null,initParams:null,userContext:null}};c.fn.media.defaults.players={flash:{name:"flash",types:"flv,mp3,swf",oAttrs:{classid:"clsid:d27cdb6e-ae6d-11cf-96b8-444553540000",type:"application/x-oleobject",codebase:"https://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version="+c.fn.media.defaults.flashVersion},eAttrs:{type:"application/x-shockwave-flash",pluginspage:"https://www.adobe.com/go/getflashplayer"}},quicktime:{name:"quicktime",types:"aif,aiff,aac,au,bmp,gsm,mov,mid,midi,mpg,mpeg,mp4,m4a,psd,qt,qtif,qif,qti,snd,tif,tiff,wav,3g2,3gp",oAttrs:{classid:"clsid:02BF25D5-8C17-4B23-BC80-D3488ABDDC6B",codebase:"https://www.apple.com/qtactivex/qtplugin.cab"},eAttrs:{pluginspage:"https://www.apple.com/quicktime/download/"}},realplayer:{name:"real",types:"ra,ram,rm,rpm,rv,smi,smil",autoplayAttr:"autostart",oAttrs:{classid:"clsid:CFCDAA03-8BE4-11cf-B84B-0020AFBBCCFA"},eAttrs:{type:"audio/x-pn-realaudio-plugin",pluginspage:"https://www.real.com/player/"}},winmedia:{name:"winmedia",types:"asf,avi,wma,wmv",autoplayAttr:"autostart",oUrl:"url",oAttrs:{classid:"clsid:6BF52A52-394A-11d3-B153-00C04F79FAA6",type:"application/x-oleobject"},eAttrs:{type:c.browser.mozilla&&k()?"application/x-ms-wmp":"application/x-mplayer2",pluginspage:"https://www.microsoft.com/Windows/MediaPlayer/"}},iframe:{name:"iframe",types:"html,pdf"},silverlight:{name:"silverlight",types:"xaml"}};function k(){var l=navigator.plugins;for(i=0;i<l.length;i++){var m=l[i];if(m.filename=="np-mswmp.dll"){return true}}return false}var a=1;for(var h in c.fn.media.defaults.players){var d=c.fn.media.defaults.players[h].types;c.each(d.split(","),function(l,m){if(e(m[0])){m="fn"+m}c.fn.media[m]=c.fn.media[h]=b(h);c.fn.media[m+"_player"]=c.fn.media.defaults.players[h]})}function j(){var m="";for(var l in c.fn.media.defaults.players){if(m.length){m+=","}m+=c.fn.media.defaults.players[l].types}return new RegExp("\\.("+m.replace(/,/g,"|")+")$\\b")}function b(l){return function(n,m){return f(n,m,l)}}function e(l){return"0123456789".indexOf(l)>-1}function g(n,y){y=y||{};var x=c(n);var v=n.className||"";var u=c.metadata?x.metadata():c.meta?x.data():{};u=u||{};var t=u.width||parseInt(((v.match(/w:(\d+)/)||[])[1]||0));var o=u.height||parseInt(((v.match(/h:(\d+)/)||[])[1]||0));if(t){u.width=t}if(o){u.height=o}if(v){u.cls=v}var s=c.fn.media.defaults;var r=y;var q=u;var m={params:{bgColor:y.bgColor||c.fn.media.defaults.bgColor}};var l=c.extend({},s,r,q);c.each(["attrs","params","flashvars","silverlight"],function(p,w){l[w]=c.extend({},m[w]||{},s[w]||{},r[w]||{},q[w]||{})});if(typeof l.caption=="undefined"){l.caption=x.text()}l.src=l.src||x.attr("href")||x.attr("src")||"unknown";return l}c.fn.media.swf=function(q,l){if(!window.SWFObject&&!window.swfobject){if(l.flashvars){var t=[];for(var r in l.flashvars){t.push(r+"="+l.flashvars[r])}if(!l.params){l.params={}}l.params.flashvars=t.join("&")}return f(q,l,"flash")}var n=q.id?(' id="'+q.id+'"'):"";var u=l.cls?(' class="'+l.cls+'"'):"";var s=c("<div"+n+u+">");if(window.swfobject){c(q).after(s).appendTo(s);if(!q.id){q.id="movie_player_"+a++}swfobject.embedSWF(l.src,q.id,l.width,l.height,l.flashVersion,l.expressInstaller,l.flashvars,l.params,l.attrs)}else{c(q).after(s).remove();var o=new SWFObject(l.src,"movie_player_"+a++,l.width,l.height,l.flashVersion,l.bgColor);if(l.expressInstaller){o.useExpressInstall(l.expressInstaller)}for(var m in l.params){if(m!="bgColor"){o.addParam(m,l.params[m])}}for(var r in l.flashvars){o.addVariable(r,l.flashvars[r])}o.write(s[0])}if(l.caption){c("<div>").appendTo(s).html(l.caption)}return s};c.fn.media.flv=c.fn.media.mp3=function(o,p){var q=p.src;var n=/\.mp3\b/i.test(q)?c.fn.media.defaults.mp3Player:c.fn.media.defaults.flvPlayer;var m=p.flvKeyName;q=encodeURIComponent(q);p.src=n;p.src=p.src+"?"+m+"="+(q);var l={};l[m]=q;p.flashvars=c.extend({},l,p.flashvars);return c.fn.media.swf(o,p)};c.fn.media.xaml=function(r,s){if(!window.Sys||!window.Sys.Silverlight){if(c.fn.media.xaml.warning){return}c.fn.media.xaml.warning=1;alert("You must include the Silverlight.js script.");return}var q={width:s.width,height:s.height,background:s.bgColor,inplaceInstallPrompt:s.silverlight.inplaceInstallPrompt,isWindowless:s.silverlight.isWindowless,framerate:s.silverlight.framerate,version:s.silverlight.version};var o={onError:s.silverlight.onError,onLoad:s.silverlight.onLoad};var p=r.id?(' id="'+r.id+'"'):"";var n=s.id||"AG"+a++;var m=s.cls?(' class="'+s.cls+'"'):"";var l=c("<div"+p+m+">");c(r).after(l).remove();Sys.Silverlight.createObjectEx({source:s.src,initParams:s.silverlight.initParams,userContext:s.silverlight.userContext,id:n,parentElement:l[0],properties:q,events:o});if(s.caption){c("<div>").appendTo(l).html(s.caption)}return l};function f(r,l,v){var y=c(r);var q=c.fn.media.defaults.players[v];if(v=="iframe"){var q=c('<iframe width="'+l.width+'" height="'+l.height+'" >');q.attr("src",l.src);q.css("backgroundColor",q.bgColor)}else{if(c.browser.msie){var u=['<object width="'+l.width+'" height="'+l.height+'" '];for(var w in l.attrs){u.push(w+'="'+l.attrs[w]+'" ')}for(var w in q.oAttrs||{}){u.push(w+'="'+q.oAttrs[w]+'" ')}u.push("></object>");var n=['<param name="'+(q.oUrl||"src")+'" value="'+l.src+'">'];for(var w in l.params){n.push('<param name="'+w+'" value="'+l.params[w]+'">')}var q=document.createElement(u.join(""));for(var s=0;s<n.length;s++){q.appendChild(document.createElement(n[s]))}}else{var u=['<embed width="'+l.width+'" height="'+l.height+'" style="display:block"'];if(l.src){u.push(' src="'+l.src+'" ')}for(var w in l.attrs){u.push(w+'="'+l.attrs[w]+'" ')}for(var w in q.eAttrs||{}){u.push(w+'="'+q.eAttrs[w]+'" ')}for(var w in l.params){u.push(w+'="'+l.params[w]+'" ')}u.push("></embed>")}}var m=r.id?(' id="'+r.id+'"'):"";var x=l.cls?(' class="'+l.cls+'"'):"";var t=c("<div"+m+x+">");y.after(t).remove();(c.browser.msie||v=="iframe")?t.append(q):t.html(u.join(""));if(l.caption){c("<div>").appendTo(t).html(l.caption)}return t}})(jQuery);


/*      SWFObject v2.2 <http://code.google.com/p/swfobject/> 
        is released under the MIT License <http://www.opensource.org/licenses/mit-license.php> 
*/
var swfobject=function(){var D="undefined",r="object",S="Shockwave Flash",W="ShockwaveFlash.ShockwaveFlash",q="application/x-shockwave-flash",R="SWFObjectExprInst",x="onreadystatechange",O=window,j=document,t=navigator,T=false,U=[h],o=[],N=[],I=[],l,Q,E,B,J=false,a=false,n,G,m=true,M=function(){var aa=typeof j.getElementById!=D&&typeof j.getElementsByTagName!=D&&typeof j.createElement!=D,ah=t.userAgent.toLowerCase(),Y=t.platform.toLowerCase(),ae=Y?/win/.test(Y):/win/.test(ah),ac=Y?/mac/.test(Y):/mac/.test(ah),af=/webkit/.test(ah)?parseFloat(ah.replace(/^.*webkit\/(\d+(\.\d+)?).*$/,"$1")):false,X=!+"\v1",ag=[0,0,0],ab=null;if(typeof t.plugins!=D&&typeof t.plugins[S]==r){ab=t.plugins[S].description;if(ab&&!(typeof t.mimeTypes!=D&&t.mimeTypes[q]&&!t.mimeTypes[q].enabledPlugin)){T=true;X=false;ab=ab.replace(/^.*\s+(\S+\s+\S+$)/,"$1");ag[0]=parseInt(ab.replace(/^(.*)\..*$/,"$1"),10);ag[1]=parseInt(ab.replace(/^.*\.(.*)\s.*$/,"$1"),10);ag[2]=/[a-zA-Z]/.test(ab)?parseInt(ab.replace(/^.*[a-zA-Z]+(.*)$/,"$1"),10):0}}else{if(typeof O.ActiveXObject!=D){try{var ad=new ActiveXObject(W);if(ad){ab=ad.GetVariable("$version");if(ab){X=true;ab=ab.split(" ")[1].split(",");ag=[parseInt(ab[0],10),parseInt(ab[1],10),parseInt(ab[2],10)]}}}catch(Z){}}}return{w3:aa,pv:ag,wk:af,ie:X,win:ae,mac:ac}}(),k=function(){if(!M.w3){return}if((typeof j.readyState!=D&&j.readyState=="complete")||(typeof j.readyState==D&&(j.getElementsByTagName("body")[0]||j.body))){f()}if(!J){if(typeof j.addEventListener!=D){j.addEventListener("DOMContentLoaded",f,false)}if(M.ie&&M.win){j.attachEvent(x,function(){if(j.readyState=="complete"){j.detachEvent(x,arguments.callee);f()}});if(O==top){(function(){if(J){return}try{j.documentElement.doScroll("left")}catch(X){setTimeout(arguments.callee,0);return}f()})()}}if(M.wk){(function(){if(J){return}if(!/loaded|complete/.test(j.readyState)){setTimeout(arguments.callee,0);return}f()})()}s(f)}}();function f(){if(J){return}try{var Z=j.getElementsByTagName("body")[0].appendChild(C("span"));Z.parentNode.removeChild(Z)}catch(aa){return}J=true;var X=U.length;for(var Y=0;Y<X;Y++){U[Y]()}}function K(X){if(J){X()}else{U[U.length]=X}}function s(Y){if(typeof O.addEventListener!=D){O.addEventListener("load",Y,false)}else{if(typeof j.addEventListener!=D){j.addEventListener("load",Y,false)}else{if(typeof O.attachEvent!=D){i(O,"onload",Y)}else{if(typeof O.onload=="function"){var X=O.onload;O.onload=function(){X();Y()}}else{O.onload=Y}}}}}function h(){if(T){V()}else{H()}}function V(){var X=j.getElementsByTagName("body")[0];var aa=C(r);aa.setAttribute("type",q);var Z=X.appendChild(aa);if(Z){var Y=0;(function(){if(typeof Z.GetVariable!=D){var ab=Z.GetVariable("$version");if(ab){ab=ab.split(" ")[1].split(",");M.pv=[parseInt(ab[0],10),parseInt(ab[1],10),parseInt(ab[2],10)]}}else{if(Y<10){Y++;setTimeout(arguments.callee,10);return}}X.removeChild(aa);Z=null;H()})()}else{H()}}function H(){var ag=o.length;if(ag>0){for(var af=0;af<ag;af++){var Y=o[af].id;var ab=o[af].callbackFn;var aa={success:false,id:Y};if(M.pv[0]>0){var ae=c(Y);if(ae){if(F(o[af].swfVersion)&&!(M.wk&&M.wk<312)){w(Y,true);if(ab){aa.success=true;aa.ref=z(Y);ab(aa)}}else{if(o[af].expressInstall&&A()){var ai={};ai.data=o[af].expressInstall;ai.width=ae.getAttribute("width")||"0";ai.height=ae.getAttribute("height")||"0";if(ae.getAttribute("class")){ai.styleclass=ae.getAttribute("class")}if(ae.getAttribute("align")){ai.align=ae.getAttribute("align")}var ah={};var X=ae.getElementsByTagName("param");var ac=X.length;for(var ad=0;ad<ac;ad++){if(X[ad].getAttribute("name").toLowerCase()!="movie"){ah[X[ad].getAttribute("name")]=X[ad].getAttribute("value")}}P(ai,ah,Y,ab)}else{p(ae);if(ab){ab(aa)}}}}}else{w(Y,true);if(ab){var Z=z(Y);if(Z&&typeof Z.SetVariable!=D){aa.success=true;aa.ref=Z}ab(aa)}}}}}function z(aa){var X=null;var Y=c(aa);if(Y&&Y.nodeName=="OBJECT"){if(typeof Y.SetVariable!=D){X=Y}else{var Z=Y.getElementsByTagName(r)[0];if(Z){X=Z}}}return X}function A(){return !a&&F("6.0.65")&&(M.win||M.mac)&&!(M.wk&&M.wk<312)}function P(aa,ab,X,Z){a=true;E=Z||null;B={success:false,id:X};var ae=c(X);if(ae){if(ae.nodeName=="OBJECT"){l=g(ae);Q=null}else{l=ae;Q=X}aa.id=R;if(typeof aa.width==D||(!/%$/.test(aa.width)&&parseInt(aa.width,10)<310)){aa.width="310"}if(typeof aa.height==D||(!/%$/.test(aa.height)&&parseInt(aa.height,10)<137)){aa.height="137"}j.title=j.title.slice(0,47)+" - Flash Player Installation";var ad=M.ie&&M.win?"ActiveX":"PlugIn",ac="MMredirectURL="+O.location.toString().replace(/&/g,"%26")+"&MMplayerType="+ad+"&MMdoctitle="+j.title;if(typeof ab.flashvars!=D){ab.flashvars+="&"+ac}else{ab.flashvars=ac}if(M.ie&&M.win&&ae.readyState!=4){var Y=C("div");X+="SWFObjectNew";Y.setAttribute("id",X);ae.parentNode.insertBefore(Y,ae);ae.style.display="none";(function(){if(ae.readyState==4){ae.parentNode.removeChild(ae)}else{setTimeout(arguments.callee,10)}})()}u(aa,ab,X)}}function p(Y){if(M.ie&&M.win&&Y.readyState!=4){var X=C("div");Y.parentNode.insertBefore(X,Y);X.parentNode.replaceChild(g(Y),X);Y.style.display="none";(function(){if(Y.readyState==4){Y.parentNode.removeChild(Y)}else{setTimeout(arguments.callee,10)}})()}else{Y.parentNode.replaceChild(g(Y),Y)}}function g(ab){var aa=C("div");if(M.win&&M.ie){aa.innerHTML=ab.innerHTML}else{var Y=ab.getElementsByTagName(r)[0];if(Y){var ad=Y.childNodes;if(ad){var X=ad.length;for(var Z=0;Z<X;Z++){if(!(ad[Z].nodeType==1&&ad[Z].nodeName=="PARAM")&&!(ad[Z].nodeType==8)){aa.appendChild(ad[Z].cloneNode(true))}}}}}return aa}function u(ai,ag,Y){var X,aa=c(Y);if(M.wk&&M.wk<312){return X}if(aa){if(typeof ai.id==D){ai.id=Y}if(M.ie&&M.win){var ah="";for(var ae in ai){if(ai[ae]!=Object.prototype[ae]){if(ae.toLowerCase()=="data"){ag.movie=ai[ae]}else{if(ae.toLowerCase()=="styleclass"){ah+=' class="'+ai[ae]+'"'}else{if(ae.toLowerCase()!="classid"){ah+=" "+ae+'="'+ai[ae]+'"'}}}}}var af="";

for(var ad in ag){if(ag[ad]!=Object.prototype[ad]){af+='<param name="'+ad+'" value="'+ag[ad]+'" />'}}
aa.outerHTML='<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"'+ah+">"+af+"</object>";N[N.length]=ai.id;X=c(ai.id)}else{var Z=C(r);Z.setAttribute("type",q);for(var ac in ai){if(ai[ac]!=Object.prototype[ac]){if(ac.toLowerCase()=="styleclass"){Z.setAttribute("class",ai[ac])}else{if(ac.toLowerCase()!="classid"){Z.setAttribute(ac,ai[ac])}}}}for(var ab in ag){if(ag[ab]!=Object.prototype[ab]&&ab.toLowerCase()!="movie"){e(Z,ab,ag[ab])}}aa.parentNode.replaceChild(Z,aa);X=Z}}return X}function e(Z,X,Y){var aa=C("param");aa.setAttribute("name",X);aa.setAttribute("value",Y);Z.appendChild(aa)}function y(Y){var X=c(Y);if(X&&X.nodeName=="OBJECT"){if(M.ie&&M.win){X.style.display="none";(function(){if(X.readyState==4){b(Y)}else{setTimeout(arguments.callee,10)}})()}else{X.parentNode.removeChild(X)}}}function b(Z){var Y=c(Z);if(Y){for(var X in Y){if(typeof Y[X]=="function"){Y[X]=null}}Y.parentNode.removeChild(Y)}}function c(Z){var X=null;try{X=j.getElementById(Z)}catch(Y){}return X}function C(X){return j.createElement(X)}function i(Z,X,Y){Z.attachEvent(X,Y);I[I.length]=[Z,X,Y]}function F(Z){var Y=M.pv,X=Z.split(".");X[0]=parseInt(X[0],10);X[1]=parseInt(X[1],10)||0;X[2]=parseInt(X[2],10)||0;return(Y[0]>X[0]||(Y[0]==X[0]&&Y[1]>X[1])||(Y[0]==X[0]&&Y[1]==X[1]&&Y[2]>=X[2]))?true:false}function v(ac,Y,ad,ab){if(M.ie&&M.mac){return}var aa=j.getElementsByTagName("head")[0];if(!aa){return}var X=(ad&&typeof ad=="string")?ad:"screen";if(ab){n=null;G=null}if(!n||G!=X){var Z=C("style");Z.setAttribute("type","text/css");Z.setAttribute("media",X);n=aa.appendChild(Z);if(M.ie&&M.win&&typeof j.styleSheets!=D&&j.styleSheets.length>0){n=j.styleSheets[j.styleSheets.length-1]}G=X}if(M.ie&&M.win){if(n&&typeof n.addRule==r){n.addRule(ac,Y)}}else{if(n&&typeof j.createTextNode!=D){n.appendChild(j.createTextNode(ac+" {"+Y+"}"))}}}function w(Z,X){if(!m){return}var Y=X?"visible":"hidden";if(J&&c(Z)){c(Z).style.visibility=Y}else{v("#"+Z,"visibility:"+Y)}}function L(Y){var Z=/[\\\"<>\.;]/;var X=Z.exec(Y)!=null;return X&&typeof encodeURIComponent!=D?encodeURIComponent(Y):Y}var d=function(){if(M.ie&&M.win){window.attachEvent("onunload",function(){var ac=I.length;for(var ab=0;ab<ac;ab++){I[ab][0].detachEvent(I[ab][1],I[ab][2])}var Z=N.length;for(var aa=0;aa<Z;aa++){y(N[aa])}for(var Y in M){M[Y]=null}M=null;for(var X in swfobject){swfobject[X]=null}swfobject=null})}}();return{registerObject:function(ab,X,aa,Z){if(M.w3&&ab&&X){var Y={};Y.id=ab;Y.swfVersion=X;Y.expressInstall=aa;Y.callbackFn=Z;o[o.length]=Y;w(ab,false)}else{if(Z){Z({success:false,id:ab})}}},getObjectById:function(X){if(M.w3){return z(X)}},embedSWF:function(ab,ah,ae,ag,Y,aa,Z,ad,af,ac){var X={success:false,id:ah};if(M.w3&&!(M.wk&&M.wk<312)&&ab&&ah&&ae&&ag&&Y){w(ah,false);K(function(){ae+="";ag+="";var aj={};if(af&&typeof af===r){for(var al in af){aj[al]=af[al]}}aj.data=ab;aj.width=ae;aj.height=ag;var am={};if(ad&&typeof ad===r){for(var ak in ad){am[ak]=ad[ak]}}if(Z&&typeof Z===r){for(var ai in Z){if(typeof am.flashvars!=D){am.flashvars+="&"+ai+"="+Z[ai]}else{am.flashvars=ai+"="+Z[ai]}}}if(F(Y)){var an=u(aj,am,ah);if(aj.id==ah){w(ah,true)}X.success=true;X.ref=an}else{if(aa&&A()){aj.data=aa;P(aj,am,ah,ac);return}else{w(ah,true)}}if(ac){ac(X)}})}else{if(ac){ac(X)}}},switchOffAutoHideShow:function(){m=false},ua:M,getFlashPlayerVersion:function(){return{major:M.pv[0],minor:M.pv[1],release:M.pv[2]}},hasFlashPlayerVersion:F,createSWF:function(Z,Y,X){if(M.w3){return u(Z,Y,X)}else{return undefined}},showExpressInstall:function(Z,aa,X,Y){if(M.w3&&A()){P(Z,aa,X,Y)}},removeSWF:function(X){if(M.w3){y(X)}},createCSS:function(aa,Z,Y,X){if(M.w3){v(aa,Z,Y,X)}},addDomLoadEvent:K,addLoadEvent:s,getQueryParamValue:function(aa){var Z=j.location.search||j.location.hash;if(Z){if(/\?/.test(Z)){Z=Z.split("?")[1]}if(aa==null){return L(Z)}var Y=Z.split("&");for(var X=0;X<Y.length;X++){if(Y[X].substring(0,Y[X].indexOf("="))==aa){return L(Y[X].substring((Y[X].indexOf("=")+1)))}}}return""},expressInstallCallback:function(){if(a){var X=c(R);if(X&&l){X.parentNode.replaceChild(l,X);if(Q){w(Q,true);if(M.ie&&M.win){l.style.display="block"}}if(E){E(B)}}a=false}}}}();

/**
* hoverIntent is similar to jQuery's built-in "hover" function except that
* instead of firing the onMouseOver event immediately, hoverIntent checks
* to see if the user's mouse has slowed down (beneath the sensitivity
* threshold) before firing the onMouseOver event.
* 
* hoverIntent r5 // 2007.03.27 // jQuery 1.1.2+
* <http://cherne.net/brian/resources/jquery.hoverIntent.html>
* 
* hoverIntent is currently available for use in all personal or commercial 
* projects under both MIT and GPL licenses. This means that you can choose 
* the license that best suits your project, and use it accordingly.
* 
* // basic usage (just like .hover) receives onMouseOver and onMouseOut functions
* $("ul li").hoverIntent( showNav , hideNav );
* 
* // advanced usage receives configuration object only
* $("ul li").hoverIntent({
*	sensitivity: 7, // number = sensitivity threshold (must be 1 or higher)
*	interval: 100,   // number = milliseconds of polling interval
*	over: showNav,  // function = onMouseOver callback (required)
*	timeout: 0,   // number = milliseconds delay before onMouseOut function call
*	out: hideNav    // function = onMouseOut callback (required)
* });
* 
* @param  f  onMouseOver function || An object with configuration options
* @param  g  onMouseOut function  || Nothing (use configuration options object)
* @author    Brian Cherne <brian@cherne.net>
*/
(function($) {
	$.fn.hoverIntent = function(f,g) {
		// default configuration options
		var cfg = {
			sensitivity: 7,
			interval: 100,
			timeout: 0
		};
		// override configuration options with user supplied object
		cfg = $.extend(cfg, g ? { over: f, out: g } : f );

		// instantiate variables
		// cX, cY = current X and Y position of mouse, updated by mousemove event
		// pX, pY = previous X and Y position of mouse, set by mouseover and polling interval
		var cX, cY, pX, pY;

		// A private function for getting mouse position
		var track = function(ev) {
			cX = ev.pageX;
			cY = ev.pageY;
		};

		// A private function for comparing current and previous mouse position
		var compare = function(ev,ob) {
			ob.hoverIntent_t = clearTimeout(ob.hoverIntent_t);
			// compare mouse positions to see if they've crossed the threshold
			if ( ( Math.abs(pX-cX) + Math.abs(pY-cY) ) < cfg.sensitivity ) {
				$(ob).unbind("mousemove",track);
				// set hoverIntent state to true (so mouseOut can be called)
				ob.hoverIntent_s = 1;
				return cfg.over.apply(ob,[ev]);
			} else {
				// set previous coordinates for next time
				pX = cX; pY = cY;
				// use self-calling timeout, guarantees intervals are spaced out properly (avoids JavaScript timer bugs)
				ob.hoverIntent_t = setTimeout( function(){compare(ev, ob);} , cfg.interval );
			}
		};

		// A private function for delaying the mouseOut function
		var delay = function(ev,ob) {
			ob.hoverIntent_t = clearTimeout(ob.hoverIntent_t);
			ob.hoverIntent_s = 0;
			return cfg.out.apply(ob,[ev]);
		};

		// A private function for handling mouse 'hovering'
		var handleHover = function(e) {
			// next three lines copied from jQuery.hover, ignore children onMouseOver/onMouseOut
			var p = (e.type == "mouseover" ? e.fromElement : e.toElement) || e.relatedTarget;
			while ( p && p != this ) { try { p = p.parentNode; } catch(e) { p = this; } }
			if ( p == this ) { return false; }

			// copy objects to be passed into t (required for event object to be passed in IE)
			var ev = jQuery.extend({},e);
			var ob = this;

			// cancel hoverIntent timer if it exists
			if (ob.hoverIntent_t) { ob.hoverIntent_t = clearTimeout(ob.hoverIntent_t); }

			// else e.type == "onmouseover"
			if (e.type == "mouseover") {
				// set "previous" X and Y position based on initial entry point
				pX = ev.pageX; pY = ev.pageY;
				// update "current" X and Y position based on mousemove
				$(ob).bind("mousemove",track);
				// start polling interval (self-calling timeout) to compare mouse coordinates over time
				if (ob.hoverIntent_s != 1) { ob.hoverIntent_t = setTimeout( function(){compare(ev,ob);} , cfg.interval );}

			// else e.type == "onmouseout"
			} else {
				// unbind expensive mousemove event
				$(ob).unbind("mousemove",track);
				// if hoverIntent state is true, then call the mouseOut function after the specified delay
				if (ob.hoverIntent_s == 1) { ob.hoverIntent_t = setTimeout( function(){delay(ev,ob);} , cfg.timeout );}
			}
		};

		// bind the function to the two event listeners
		return this.mouseover(handleHover).mouseout(handleHover);
	};
})(jQuery);

/* Copyright (c) 2006 Brandon Aaron (http://brandonaaron.net)
 * Dual licensed under the MIT (http://www.opensource.org/licenses/mit-license.php) 
 * and GPL (http://www.opensource.org/licenses/gpl-license.php) licenses.
 *
 * $LastChangedDate: 2007-06-19 20:25:28 -0500 (Tue, 19 Jun 2007) $
 * $Rev: 2111 $
 *
 * Version 2.1
 */
(function($){$.fn.bgIframe=$.fn.bgiframe=function(s){if($.browser.msie&&parseInt($.browser.version)<=6){s=$.extend({top:'auto',left:'auto',width:'auto',height:'auto',opacity:true,src:'javascript:false;'},s||{});var prop=function(n){return n&&n.constructor==Number?n+'px':n;},html='<iframe class="bgiframe"frameborder="0"tabindex="-1"src="'+s.src+'"'+'style="display:block;position:absolute;z-index:-1;'+(s.opacity!==false?'filter:Alpha(Opacity=\'0\');':'')+'top:'+(s.top=='auto'?'expression(((parseInt(this.parentNode.currentStyle.borderTopWidth)||0)*-1)+\'px\')':prop(s.top))+';'+'left:'+(s.left=='auto'?'expression(((parseInt(this.parentNode.currentStyle.borderLeftWidth)||0)*-1)+\'px\')':prop(s.left))+';'+'width:'+(s.width=='auto'?'expression(this.parentNode.offsetWidth+\'px\')':prop(s.width))+';'+'height:'+(s.height=='auto'?'expression(this.parentNode.offsetHeight+\'px\')':prop(s.height))+';'+'"/>';return this.each(function(){if($('> iframe.bgiframe',this).length==0)this.insertBefore(document.createElement(html),this.firstChild);});}return this;};if(!$.browser.version)$.browser.version=navigator.userAgent.toLowerCase().match(/.+(?:rv|it|ra|ie)[\/: ]([\d.]+)/)[1];})(jQuery);

/*
 *
 * Copyright (c) 2006/2007 Sam Collett (http://www.texotela.co.uk)
 * Licensed under the MIT License:
 * http://www.opensource.org/licenses/mit-license.php
 * 
 * Version 2.0
 * Demo: http://www.texotela.co.uk/code/jquery/newsticker/
 *
 * $LastChangedDate$
 * $Rev$
 *
 */
 
(function($) {
/*
 * A basic news ticker.
 *
 * @name     newsticker (or newsTicker)
 * @param    delay      Delay (in milliseconds) between iterations. Default 4 seconds (4000ms)
 * @author   Sam Collett (http://www.texotela.co.uk)
 * @example  $("#news").newsticker(); // or $("#news").newsTicker(5000);
 *
 */
$.fn.newsTicker = $.fn.newsticker = function(delay)
{
	delay = delay || 4000;
	initTicker = function(el)
	{
		stopTicker(el);
		el.items = $("li", el);
		// hide all items (except first one)
		el.items.not(":eq(0)").hide().end();
		// current item
		el.currentitem = 0;
		startTicker(el);
	};
	startTicker = function(el)
	{
		el.tickfn = setInterval(function() { doTick(el) }, delay)
	};
	stopTicker = function(el)
	{
		clearInterval(el.tickfn);
	};
	pauseTicker = function(el)
	{
		el.pause = true;
	};
	resumeTicker = function(el)
	{
		el.pause = false;
	};
	doTick = function(el)
	{
		// don't run if paused
		if(el.pause) return;
		// pause until animation has finished
		el.pause = true;
		// hide current item
		$(el.items[el.currentitem]).fadeOut("slow",
			function()
			{
				$(this).hide();
				// move to next item and show
				el.currentitem = ++el.currentitem % (el.items.size());
				$(el.items[el.currentitem]).fadeIn("slow",
					function()
					{
						el.pause = false;
					}
				);
			}
		);
	};
	this.each(
		function()
		{
			if(this.nodeName.toLowerCase()!= "ul") return;
			initTicker(this);
		}
	)
	.addClass("newsticker")
	.hover(
		function()
		{
			// pause if hovered over
			pauseTicker(this);
		},
		function()
		{
			// resume when not hovered over
			resumeTicker(this);
		}
	);
	return this;
};

})(jQuery);

/*
 * jQuery history plugin
 * 
 * sample page: http://www.mikage.to/jquery/jquery_history.html
 *
 * Copyright (c) 2006-2009 Taku Sano (Mikage Sawatari)
 * Licensed under the MIT License:
 *   http://www.opensource.org/licenses/mit-license.php
 *
 * Modified by Lincoln Cooper to add Safari support and only call the callback once during initialization
 * for msie when no initial hash supplied.
 */


jQuery.extend({
	historyCurrentHash: undefined,
	historyCallback: undefined,
	historyIframeSrc: undefined,
	historyNeedIframe: jQuery.browser.msie && (jQuery.browser.version < 8 || document.documentMode < 8),
	
	historyInit: function(callback, src){
		jQuery.historyCallback = callback;
		if (src) jQuery.historyIframeSrc = src;
		var current_hash = location.hash.replace(/\?.*$/, '');
		
		jQuery.historyCurrentHash = current_hash;
		if (jQuery.historyNeedIframe) {
			// To stop the callback firing twice during initilization if no hash present
			if (jQuery.historyCurrentHash == '') {
				jQuery.historyCurrentHash = '#';
			}
		
			// add hidden iframe for IE
			jQuery("body").prepend('<iframe id="jQuery_history" style="display: none;"'+
				' src="javascript:false;"></iframe>'
			);
			var ihistory = jQuery("#jQuery_history")[0];
			var iframe = ihistory.contentWindow.document;
			iframe.open();
			iframe.close();
			iframe.location.hash = current_hash;
		}
		else if (jQuery.browser.safari) {
			// etablish back/forward stacks
			jQuery.historyBackStack = [];
			jQuery.historyBackStack.length = history.length;
			jQuery.historyForwardStack = [];
			jQuery.lastHistoryLength = history.length;
			
			jQuery.isFirst = true;
		}
		if(current_hash)
			jQuery.historyCallback(current_hash.replace(/^#/, ''));
		setInterval(jQuery.historyCheck, 100);
	},
	
	historyAddHistory: function(hash) {
		// This makes the looping function do something
		jQuery.historyBackStack.push(hash);
		
		jQuery.historyForwardStack.length = 0; // clear forwardStack (true click occured)
		this.isFirst = true;
	},
	
	historyCheck: function(){
		if (jQuery.historyNeedIframe) {
			// On IE, check for location.hash of iframe
			var ihistory = jQuery("#jQuery_history")[0];
			var iframe = ihistory.contentDocument || ihistory.contentWindow.document;
			var current_hash = iframe.location.hash.replace(/\?.*$/, '');
			if(current_hash != jQuery.historyCurrentHash) {
			
				location.hash = current_hash;
				jQuery.historyCurrentHash = current_hash;
				jQuery.historyCallback(current_hash.replace(/^#/, ''));
				
			}
		} else if (jQuery.browser.safari) {
			if(jQuery.lastHistoryLength == history.length && jQuery.historyBackStack.length > jQuery.lastHistoryLength) {
				jQuery.historyBackStack.shift();
			}
			if (!jQuery.dontCheck) {
				var historyDelta = history.length - jQuery.historyBackStack.length;
				jQuery.lastHistoryLength = history.length;
				
				if (historyDelta) { // back or forward button has been pushed
					jQuery.isFirst = false;
					if (historyDelta < 0) { // back button has been pushed
						// move items to forward stack
						for (var i = 0; i < Math.abs(historyDelta); i++) jQuery.historyForwardStack.unshift(jQuery.historyBackStack.pop());
					} else { // forward button has been pushed
						// move items to back stack
						for (var i = 0; i < historyDelta; i++) jQuery.historyBackStack.push(jQuery.historyForwardStack.shift());
					}
					var cachedHash = jQuery.historyBackStack[jQuery.historyBackStack.length - 1];
					if (cachedHash != undefined) {
						jQuery.historyCurrentHash = location.hash.replace(/\?.*$/, '');
						jQuery.historyCallback(cachedHash);
					}
				} else if (jQuery.historyBackStack[jQuery.historyBackStack.length - 1] == undefined && !jQuery.isFirst) {
					// back button has been pushed to beginning and URL already pointed to hash (e.g. a bookmark)
					// document.URL doesn't change in Safari
					if (location.hash) {
						var current_hash = location.hash;
						jQuery.historyCallback(location.hash.replace(/^#/, ''));
					} else {
						var current_hash = '';
						jQuery.historyCallback('');
					}
					jQuery.isFirst = true;
				}
			}
		} else {
			// otherwise, check for location.hash
			var current_hash = location.hash.replace(/\?.*$/, '');
			if(current_hash != jQuery.historyCurrentHash) {
				jQuery.historyCurrentHash = current_hash;
				jQuery.historyCallback(current_hash.replace(/^#/, ''));
			}
		}
	},
	historyLoad: function(hash){
		var newhash;
		hash = decodeURIComponent(hash.replace(/\?.*$/, ''));
		
		if (jQuery.browser.safari) {
			newhash = hash;
		}
		else {
			newhash = '#' + hash;
			location.hash = newhash;
		}
		jQuery.historyCurrentHash = newhash;
		
		if (jQuery.historyNeedIframe) {
			var ihistory = jQuery("#jQuery_history")[0];
			var iframe = ihistory.contentWindow.document;
			iframe.open();
			iframe.close();
			iframe.location.hash = newhash;
			jQuery.lastHistoryLength = history.length;
			jQuery.historyCallback(hash);
		}
		else if (jQuery.browser.safari) {
			jQuery.dontCheck = true;
			// Manually keep track of the history values for Safari
			this.historyAddHistory(hash);
			
			// Wait a while before allowing checking so that Safari has time to update the "history" object
			// correctly (otherwise the check loop would detect a false change in hash).
			var fn = function() {jQuery.dontCheck = false;};
			window.setTimeout(fn, 200);
			jQuery.historyCallback(hash);
			// N.B. "location.hash=" must be the last line of code for Safari as execution stops afterwards.
			//      By explicitly using the "location.hash" command (instead of using a variable set to "location.hash") the
			//      URL in the browser and the "history" object are both updated correctly.
			location.hash = newhash;
		}
		else {
		  jQuery.historyCallback(hash);
		}
	}
});

/* 
    jQuery DOM window - lightbox
*/
(function($){
	
	//closeDOMWindow
	$.fn.closeDOMWindow = function(settings){
		
		if(!settings){settings={};}
		
		var run = function(passingThis){
			
			if(settings.anchoredClassName){
				var $anchorClassName = $('.'+settings.anchoredClassName);
				$anchorClassName.fadeOut('fast',function(){
					if($.fn.draggable){
						$anchorClassName.draggable('destory').trigger("unload").remove();	
					}else{
						$anchorClassName.trigger("unload").remove();
					}
				});
				if(settings.functionCallOnClose){settings.functionCallAfterClose();}
			}else{
				var $DOMWindowOverlay = $('#DOMWindowOverlay');
				var $DOMWindow = $('#DOMWindow');
				$DOMWindowOverlay.fadeOut('fast',function(){
					$DOMWindowOverlay.trigger('unload').unbind().remove();																	  
				});
				$DOMWindow.fadeOut('fast',function(){
					if($.fn.draggable){
						$DOMWindow.draggable("destroy").trigger("unload").remove();
					}else{
						$DOMWindow.trigger("unload").remove();
					}
				});
			
				$(window).unbind('scroll.DOMWindow');
				$(window).unbind('resize.DOMWindow');
				
				if($.fn.openDOMWindow.isIE6){$('#DOMWindowIE6FixIframe').remove();}
				if(settings.functionCallOnClose){settings.functionCallAfterClose();}
			}
            $('#dom-closer').remove();			
		};
		
		if(settings.eventType){//if used with $().
			return this.each(function(index){
				$(this).bind(settings.eventType, function(){
					run(this);
					return false;
				});
			});
		}else{//else called as $.function
			run();
		}
		
	};
	
	//allow for public call, pass settings
	$.closeDOMWindow = function(s){$.fn.closeDOMWindow(s);};
	
	//openDOMWindow
	$.fn.openDOMWindow = function(instanceSettings){	
		
		var shortcut =  $.fn.openDOMWindow;
	
		//default settings combined with callerSettings////////////////////////////////////////////////////////////////////////
		
		shortcut.defaultsSettings = {
			anchoredClassName:'',
			anchoredSelector:'',
			borderColor:'#ccc',
			borderSize:'4',
			draggable:0,
			eventType:null, //click, blur, change, dblclick, error, focus, load, mousedown, mouseout, mouseup etc...
			fixedWindowY:100,
			functionCallOnOpen:null,
			functionCallOnClose:null,
			height:500,
			loader:0,
			loaderHeight:0,
			loaderImagePath:'/_public/shared/images/loader_white.gif',
			loaderWidth:0,
			modal:0,
			overlay:1,
			overlayColor:'#000',
			overlayOpacity:'50',
			positionLeft:0,
			positionTop:0,
			positionType:'centered', // centered, anchored, absolute, fixed
			width:500, 
			windowBGColor:'#fff',
			windowBGImage:null, // http path
			windowHTTPType:'get',
			windowPadding:10,
			windowSource:'inline', //inline, ajax, iframe
			windowSourceID:'',
			windowSourceURL:'',
			windowSourceAttrURL:'href'
		};
		
		var settings = $.extend({}, $.fn.openDOMWindow.defaultsSettings , instanceSettings || {});
		
		//Public functions
		
		shortcut.viewPortHeight = function(){ return self.innerHeight || document.documentElement.clientHeight || document.body.clientHeight;};
		shortcut.viewPortWidth = function(){ return self.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;};
		shortcut.scrollOffsetHeight = function(){ return self.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop;};
		shortcut.scrollOffsetWidth = function(){ return self.pageXOffset || document.documentElement.scrollLeft || document.body.scrollLeft;};
		shortcut.isIE6 = typeof document.body.style.maxHeight === "undefined";
		
		//Private Functions/////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		var sizeOverlay = function(){
			var $DOMWindowOverlay = $('#DOMWindowOverlay');
			if(shortcut.isIE6){//if IE 6
				var overlayViewportHeight = document.documentElement.offsetHeight + document.documentElement.scrollTop - 4;
				var overlayViewportWidth = document.documentElement.offsetWidth - 21;
				$DOMWindowOverlay.css({'height':overlayViewportHeight +'px','width':overlayViewportWidth+'px'});
			}else{//else Firefox, safari, opera, IE 7+
				$DOMWindowOverlay.css({'height':'100%','width':'100%','position':'fixed'});
			}	
		};
		
		var sizeIE6Iframe = function(){
			var overlayViewportHeight = document.documentElement.offsetHeight + document.documentElement.scrollTop - 4;
			var overlayViewportWidth = document.documentElement.offsetWidth - 21;
			$('#DOMWindowIE6FixIframe').css({'height':overlayViewportHeight +'px','width':overlayViewportWidth+'px'});
		};
		
		var centerDOMWindow = function() {
			var $DOMWindow = $('#DOMWindow');
			if(settings.height + 50 > shortcut.viewPortHeight()){//added 50 to be safe
				$DOMWindow.css('left',Math.round(shortcut.viewPortWidth()/2) + shortcut.scrollOffsetWidth() - Math.round(($DOMWindow.outerWidth())/2));
			}else{
				$DOMWindow.css('left',Math.round(shortcut.viewPortWidth()/2) + shortcut.scrollOffsetWidth() - Math.round(($DOMWindow.outerWidth())/2));
				$DOMWindow.css('top',Math.round(shortcut.viewPortHeight()/2) + shortcut.scrollOffsetHeight() - Math.round(($DOMWindow.outerHeight())/2));
			}
			// Reposition dom-closer
			var domPos = $DOMWindow.offset();
			domPos['top'] = domPos['top'] - 20;
			domPos['left'] = domPos['left'] + domWidth - 15;	
			var domWidth = $DOMWindow.outerWidth();;
			$('#dom-closer').css(domPos);
			$('#dom-closer').width(30);
		};
		
		var centerLoader = function() {
			var $DOMWindowLoader = $('#DOMWindowLoader');
			if(shortcut.isIE6){//if IE 6
				$DOMWindowLoader.css({'left':Math.round(shortcut.viewPortWidth()/2) + shortcut.scrollOffsetWidth() - Math.round(($DOMWindowLoader.innerWidth())/2),'position':'absolute'});
				$DOMWindowLoader.css({'top':Math.round(shortcut.viewPortHeight()/2) + shortcut.scrollOffsetHeight() - Math.round(($DOMWindowLoader.innerHeight())/2),'position':'absolute'});
			}else{
				$DOMWindowLoader.css({'left':'50%','top':'50%','position':'fixed'});
			}
			
		};
		
		var fixedDOMWindow = function(){
			var $DOMWindow = $('#DOMWindow');
			$DOMWindow.css('left', settings.positionLeft + shortcut.scrollOffsetWidth());
			$DOMWindow.css('top', + settings.positionTop + shortcut.scrollOffsetHeight());
		};
		
		var showDOMWindow = function(instance){
			if(arguments[0]){
				$('.'+instance+' #DOMWindowLoader').remove();
				$('.'+instance+' #DOMWindowContent').fadeIn('fast',function(){if(settings.functionCallOnOpen){settings.functionCallOnOpen();}});
				$('.'+instance+ '.closeDOMWindow').click(function(){
					$.closeDOMWindow();	
					return false;
				});
			}else{
				$('#DOMWindowLoader').remove();
				$('#DOMWindow').fadeIn('fast',function(){if(settings.functionCallOnOpen){settings.functionCallOnOpen();}});
				$('#DOMWindow .closeDOMWindow').click(function(){						
					$.closeDOMWindow();
					return false;
				});
			}
			
		};
		
		var urlQueryToObject = function(s){
			  var query = {};
			  s.replace(/b([^&=]*)=([^&=]*)b/g, function (m, a, d) {
				if (typeof query[a] != 'undefined') {
				  query[a] += ',' + d;
				} else {
				  query[a] = d;
				}
			  });
			  return query;
		};
			
		//Run Routine ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
		var run = function(passingThis){
			
			//get values from element clicked, or assume its passed as an option
			settings.windowSourceID = $(passingThis).attr('href') || settings.windowSourceID;
			settings.windowSourceURL = $(passingThis).attr(settings.windowSourceAttrURL) || settings.windowSourceURL;
			settings.windowBGImage = settings.windowBGImage ? 'background-image:url('+settings.windowBGImage+')' : '';
			var urlOnly, urlQueryObject;
			
			if(settings.positionType == 'anchored'){//anchored DOM window
				
				var anchoredPositions = $(settings.anchoredSelector).position();
				var anchoredPositionX = anchoredPositions.left + settings.positionLeft;
				var anchoredPositionY = anchoredPositions.top + settings.positionTop;
				
				$('body').append('<div class="'+settings.anchoredClassName+'" style="'+settings.windowBGImage+';background-repeat:no-repeat;padding:'+settings.windowPadding+'px;overflow:auto;position:absolute;top:'+anchoredPositionY+'px;left:'+anchoredPositionX+'px;height:'+settings.height+'px;width:'+settings.width+'px;background-color:'+settings.windowBGColor+';border:'+settings.borderSize+'px solid '+settings.borderColor+';z-index:10001"><div id="DOMWindowContent" style="display:none"></div></div>');		
				//loader
				if(settings.loader && settings.loaderImagePath !== ''){
					$('.'+settings.anchoredClassName).append('<div id="DOMWindowLoader" style="width:'+settings.loaderWidth+'px;height:'+settings.loaderHeight+'px;"><img src="'+settings.loaderImagePath+'" /></div>');
					
				}

				if($.fn.draggable){
					if(settings.draggable){$('.' + settings.anchoredClassName).draggable({cursor:'move'});}
				}
				
				switch(settings.windowSource){
					case 'inline'://////////////////////////////// inline //////////////////////////////////////////
						$('.' + settings.anchoredClassName+" #DOMWindowContent").append($(settings.windowSourceID).children());
						$('.' + settings.anchoredClassName).unload(function(){// move elements back when you're finished
							$('.' + settings.windowSourceID).append( $('.' + settings.anchoredClassName+" #DOMWindowContent").children());				
						});
						showDOMWindow(settings.anchoredClassName);
					break;
					case 'iframe'://////////////////////////////// iframe //////////////////////////////////////////
						$('.' + settings.anchoredClassName+" #DOMWindowContent").append('<iframe frameborder="0" hspace="0" wspace="0" src="'+settings.windowSourceURL+'" name="DOMWindowIframe'+Math.round(Math.random()*1000)+'" style="width:100%;height:100%;border:none;background-color:#fff;" class="'+settings.anchoredClassName+'Iframe" ></iframe>');
						$('.'+settings.anchoredClassName+'Iframe').load(showDOMWindow(settings.anchoredClassName));
					break;
					case 'ajax'://////////////////////////////// ajax //////////////////////////////////////////	
						if(settings.windowHTTPType == 'post'){
							
							if(settings.windowSourceURL.indexOf("?") !== -1){//has a query string
								urlOnly = settings.windowSourceURL.substr(0, settings.windowSourceURL.indexOf("?"));
								urlQueryObject = urlQueryToObject(settings.windowSourceURL);
							}else{
								urlOnly = settings.windowSourceURL;
								urlQueryObject = {};
							}
							$('.' + settings.anchoredClassName+" #DOMWindowContent").load(urlOnly,urlQueryObject,function(){
								showDOMWindow(settings.anchoredClassName);
							});
						}else{
							if(settings.windowSourceURL.indexOf("?") == -1){ //no query string, so add one
								settings.windowSourceURL += '?';
							}
							$('.' + settings.anchoredClassName+" #DOMWindowContent").load(
								settings.windowSourceURL + '&random=' + (new Date().getTime()),function(){
								showDOMWindow(settings.anchoredClassName);
							});
						}
					break;
				}
				
			}else{//centered, fixed, absolute DOM window
				
				//overlay & modal
				if(settings.overlay){
					$('body').append('<div id="DOMWindowOverlay" style="z-index:10000;display:none;position:absolute;top:0;left:0;background-color:'+settings.overlayColor+';filter:alpha(opacity='+settings.overlayOpacity+');-moz-opacity: 0.'+settings.overlayOpacity+';opacity: 0.'+settings.overlayOpacity+';"></div>');
					if(shortcut.isIE6){//if IE 6
						$('body').append('<iframe id="DOMWindowIE6FixIframe"  src="blank.html"  style="width:100%;height:100%;z-index:9999;position:absolute;top:0;left:0;filter:alpha(opacity=0);"></iframe>');
						sizeIE6Iframe();
					}
					sizeOverlay();
					var $DOMWindowOverlay = $('#DOMWindowOverlay');
					$DOMWindowOverlay.fadeIn('fast');
					if(!settings.modal){$DOMWindowOverlay.click(function(){$.closeDOMWindow();});}
				}
				
				//loader
				if(settings.loader && settings.loaderImagePath !== ''){
					$('body').append('<div id="DOMWindowLoader" style="z-index:10002;width:'+settings.loaderWidth+'px;height:'+settings.loaderHeight+'px;"><img src="'+settings.loaderImagePath+'" /></div>');
					centerLoader();
				}

				//add DOMwindow
				$('body').append('<div id="DOMWindow" style="background-repeat:no-repeat;'+settings.windowBGImage+';overflow:auto;padding:'+settings.windowPadding+'px;display:none;height:'+settings.height+'px;width:'+settings.width+'px;background-color:'+settings.windowBGColor+';border:'+settings.borderSize+'px solid '+settings.borderColor+'; position:absolute;z-index:10001"></div>');
				
				var $DOMWindow = $('#DOMWindow');
				//centered, absolute, or fixed
				switch(settings.positionType){
					case 'centered':
						centerDOMWindow();
						if(settings.height + 50 > shortcut.viewPortHeight()){//added 50 to be safe
							//alert(shortcut.viewPortHeight());
							$DOMWindow.css('top', (settings.fixedWindowY + shortcut.scrollOffsetHeight()) + 'px');
						}
					break;
					case 'absolute':
						$DOMWindow.css({'top':(settings.positionTop+shortcut.scrollOffsetHeight())+'px','left':(settings.positionLeft+shortcut.scrollOffsetWidth())+'px'});
						if($.fn.draggable){
							if(settings.draggable){$DOMWindow.draggable({cursor:'move'});}
						}
					break;
					case 'fixed':
						fixedDOMWindow();
					break;
					case 'anchoredSingleWindow':
						var anchoredPositions = $(settings.anchoredSelector).position();
						var anchoredPositionX = anchoredPositions.left + settings.positionLeft;
						var anchoredPositionY = anchoredPositions.top + settings.positionTop;
						$DOMWindow.css({'top':anchoredPositionY + 'px','left':anchoredPositionX+'px'});
								
					break;
				}
				
				$(window).bind('scroll.DOMWindow',function(){
					if(settings.overlay){sizeOverlay();}
					if(shortcut.isIE6){sizeIE6Iframe();}
					if(settings.positionType == 'centered'){centerDOMWindow();}
					if(settings.positionType == 'fixed'){fixedDOMWindow();}
				});

				$(window).bind('resize.DOMWindow',function(){
					if(shortcut.isIE6){sizeIE6Iframe();}
					if(settings.overlay){sizeOverlay();}
					if(settings.positionType == 'centered'){centerDOMWindow();}
				});
				
				switch(settings.windowSource){
					case 'inline'://////////////////////////////// inline //////////////////////////////////////////
						$DOMWindow.append($(settings.windowSourceID).children());
						$DOMWindow.unload(function(){// move elements back when you're finished
							$(settings.windowSourceID).append($DOMWindow.children());				
						});
						showDOMWindow();
					break;
					case 'iframe'://////////////////////////////// iframe //////////////////////////////////////////
						$DOMWindow.append('<iframe frameborder="0" hspace="0" wspace="0" src="'+settings.windowSourceURL+'" name="DOMWindowIframe'+Math.round(Math.random()*1000)+'" style="width:100%;height:100%;border:none;background-color:#fff;" id="DOMWindowIframe" ></iframe>');
						$('#DOMWindowIframe').load(showDOMWindow());
					break;
					case 'ajax'://////////////////////////////// ajax //////////////////////////////////////////
						if(settings.windowHTTPType == 'post'){
							
							if(settings.windowSourceURL.indexOf("?") !== -1){//has a query string
								urlOnly = settings.windowSourceURL.substr(0, settings.windowSourceURL.indexOf("?"));
								urlQueryObject = urlQueryToObject(settings.windowSourceURL);
							}else{
								urlOnly = settings.windowSourceURL;
								urlQueryObject = {};
							}
							$DOMWindow.load(urlOnly,urlQueryObject,function(){
								showDOMWindow();
							});
						}else{
							if(settings.windowSourceURL.indexOf("?") == -1){ //no query string, so add one
								settings.windowSourceURL += '?';
							}
							$DOMWindow.load(
								settings.windowSourceURL + '&random=' + (new Date().getTime()),function(){
								showDOMWindow();
							});
						}
					break;
				}
				
			}//end if anchored, or absolute, fixed, centered
			
			//Custom close box (ljl - g012328)
			var domPos = $DOMWindow.offset();
			var domWidth = $DOMWindow.outerWidth();;
			domPos['top'] = domPos['top'] - 20;
			domPos['left'] = domPos['left'] + domWidth - 15;
			$('body').append('<p style="position:absolute;z-index:999999;text-align:right;font-size:13px;" id="dom-closer"><a href="#"><span>X</span></a></p>');
			$('#dom-closer').css(domPos);
			$('#dom-closer').width('30');
			$('#dom-closer a').click(function(e) {
			     e.preventDefault();
			     $.closeDOMWindow();
			});
		};//end run()
		
		if(settings.eventType){//if used with $().
			return this.each(function(index){				  
				$(this).bind(settings.eventType,function(){
					run(this);
					return false;
				});
			});	
		}else{//else called as $.function
			run();
		}
		
	};//end function openDOMWindow
	
	//allow for public call, pass settings
	$.openDOMWindow = function(s){$.fn.openDOMWindow(s);};
	
})(jQuery);

/*
 * jQuery Cycle Lite Plugin
 * http://malsup.com/jquery/cycle/lite/
 * Copyright (c) 2008-2011 M. Alsup
 * Version: 1.1 (03/07/2011)
 * Dual licensed under the MIT and GPL licenses:
 * http://www.opensource.org/licenses/mit-license.php
 * http://www.gnu.org/licenses/gpl.html
 * Requires: jQuery v1.3.2 or later
 */
;(function($){var ver="Lite-1.1";$.fn.cycle=function(options){return this.each(function(){options=options||{};if(this.cycleTimeout){clearTimeout(this.cycleTimeout);}this.cycleTimeout=0;this.cyclePause=0;var $cont=$(this);var $slides=options.slideExpr?$(options.slideExpr,this):$cont.children();var els=$slides.get();if(els.length<2){window.console&&console.log("terminating; too few slides: "+els.length);return ;}var opts=$.extend({},$.fn.cycle.defaults,options||{},$.metadata?$cont.metadata():$.meta?$cont.data():{});opts.before=opts.before?[opts.before]:[];opts.after=opts.after?[opts.after]:[];opts.after.unshift(function(){opts.busy=0;});var cls=this.className;opts.width=parseInt((cls.match(/w:(\d+)/)||[])[1])||opts.width;opts.height=parseInt((cls.match(/h:(\d+)/)||[])[1])||opts.height;opts.timeout=parseInt((cls.match(/t:(\d+)/)||[])[1])||opts.timeout;if($cont.css("position")=="static"){$cont.css("position","relative");}if(opts.width){$cont.width(opts.width);}if(opts.height&&opts.height!="auto"){$cont.height(opts.height);}var first=0;$slides.css({position:"absolute",top:0,left:0}).each(function(i){$(this).css("z-index",els.length-i);});$(els[first]).css("opacity",1).show();if($.browser.msie){els[first].style.removeAttribute("filter");}if(opts.fit&&opts.width){$slides.width(opts.width);}if(opts.fit&&opts.height&&opts.height!="auto"){$slides.height(opts.height);}if(opts.pause){$cont.hover(function(){this.cyclePause=1;},function(){this.cyclePause=0;});}var txFn=$.fn.cycle.transitions[opts.fx];txFn&&txFn($cont,$slides,opts);$slides.each(function(){var $el=$(this);this.cycleH=(opts.fit&&opts.height)?opts.height:$el.height();this.cycleW=(opts.fit&&opts.width)?opts.width:$el.width();});if(opts.cssFirst){$($slides[first]).css(opts.cssFirst);}if(opts.timeout){if(opts.speed.constructor==String){opts.speed={slow:600,fast:200}[opts.speed]||400;}if(!opts.sync){opts.speed=opts.speed/2;}while((opts.timeout-opts.speed)<250){opts.timeout+=opts.speed;}}opts.speedIn=opts.speed;opts.speedOut=opts.speed;opts.slideCount=els.length;opts.currSlide=first;opts.nextSlide=1;var e0=$slides[first];if(opts.before.length){opts.before[0].apply(e0,[e0,e0,opts,true]);}if(opts.after.length>1){opts.after[1].apply(e0,[e0,e0,opts,true]);}if(opts.click&&!opts.next){opts.next=opts.click;}if(opts.next){$(opts.next).bind("click",function(){return advance(els,opts,opts.rev?-1:1);});}if(opts.prev){$(opts.prev).bind("click",function(){return advance(els,opts,opts.rev?1:-1);});}if(opts.timeout){this.cycleTimeout=setTimeout(function(){go(els,opts,0,!opts.rev);},opts.timeout+(opts.delay||0));}});};function go(els,opts,manual,fwd){if(opts.busy){return ;}var p=els[0].parentNode,curr=els[opts.currSlide],next=els[opts.nextSlide];if(p.cycleTimeout===0&&!manual){return ;}if(manual||!p.cyclePause){if(opts.before.length){$.each(opts.before,function(i,o){o.apply(next,[curr,next,opts,fwd]);});}var after=function(){if($.browser.msie){this.style.removeAttribute("filter");}$.each(opts.after,function(i,o){o.apply(next,[curr,next,opts,fwd]);});};if(opts.nextSlide!=opts.currSlide){opts.busy=1;$.fn.cycle.custom(curr,next,opts,after);}var roll=(opts.nextSlide+1)==els.length;opts.nextSlide=roll?0:opts.nextSlide+1;opts.currSlide=roll?els.length-1:opts.nextSlide-1;}if(opts.timeout){p.cycleTimeout=setTimeout(function(){go(els,opts,0,!opts.rev);},opts.timeout);}}function advance(els,opts,val){var p=els[0].parentNode,timeout=p.cycleTimeout;if(timeout){clearTimeout(timeout);p.cycleTimeout=0;}opts.nextSlide=opts.currSlide+val;if(opts.nextSlide<0){opts.nextSlide=els.length-1;}else{if(opts.nextSlide>=els.length){opts.nextSlide=0;}}go(els,opts,1,val>=0);return false;}$.fn.cycle.custom=function(curr,next,opts,cb){var $l=$(curr),$n=$(next);$n.css(opts.cssBefore);var fn=function(){$n.animate(opts.animIn,opts.speedIn,opts.easeIn,cb);};$l.animate(opts.animOut,opts.speedOut,opts.easeOut,function(){$l.css(opts.cssAfter);if(!opts.sync){fn();}});if(opts.sync){fn();}};$.fn.cycle.transitions={fade:function($cont,$slides,opts){opts.cssBefore={opacity:0};opts.animOut={opacity:0};opts.animIn={opacity:1};}};$.fn.cycle.ver=function(){return ver;};$.fn.cycle.defaults={fx:"fade",timeout:4000,speed:1000,next:null,prev:null,before:null,after:null,height:"auto",sync:1,fit:0,pause:0,delay:0,slideExpr:null,cssBefore:{},cssAfter:{},animIn:{},animOut:{}};})(jQuery);
/* ==========================================================
 * bootstrap-twipsy.js v1.3.0
 * http://twitter.github.com/bootstrap/javascript.html#twipsy
 * Adapted from the original jQuery.tipsy by Jason Frame
 * ==========================================================
 * Copyright 2011 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ========================================================== */


!function( $ ) {

 /* CSS TRANSITION SUPPORT (https://gist.github.com/373874)
  * ======================================================= */

  var transitionEnd

  $(document).ready(function () {

    $.support.transition = (function () {
      var thisBody = document.body || document.documentElement
        , thisStyle = thisBody.style
        , support = thisStyle.transition !== undefined || thisStyle.WebkitTransition !== undefined || thisStyle.MozTransition !== undefined || thisStyle.MsTransition !== undefined || thisStyle.OTransition !== undefined
      return support
    })()

    // set CSS transition event type
    if ( $.support.transition ) {
      transitionEnd = "TransitionEnd"
      if ( $.browser.webkit ) {
      	transitionEnd = "webkitTransitionEnd"
      } else if ( $.browser.mozilla ) {
      	transitionEnd = "transitionend"
      } else if ( $.browser.opera ) {
      	transitionEnd = "oTransitionEnd"
      }
    }

  })


 /* TWIPSY PUBLIC CLASS DEFINITION
  * ============================== */

  var Twipsy = function ( element, options ) {
    this.$element = $(element)
    this.options = options
    this.enabled = true
    this.fixTitle()
  }

  Twipsy.prototype = {

    show: function() {
      var pos
        , actualWidth
        , actualHeight
        , placement
        , $tip
        , tp

      if (this.getTitle() && this.enabled) {
        $tip = this.tip()
        this.setContent()

        if (this.options.animate) {
          $tip.addClass('fade')
        }

        $tip
          .remove()
          .css({ top: 0, left: 0, display: 'block' })
          .prependTo(document.body)

        pos = $.extend({}, this.$element.offset(), {
          width: this.$element[0].offsetWidth
        , height: this.$element[0].offsetHeight
        })

        actualWidth = $tip[0].offsetWidth
        actualHeight = $tip[0].offsetHeight
        placement = _.maybeCall(this.options.placement, this.$element[0])

        switch (placement) {
          case 'below':
            tp = {top: pos.top + pos.height + this.options.offset, left: pos.left + pos.width / 2 - actualWidth / 2}
            break
          case 'above':
            tp = {top: pos.top - actualHeight - this.options.offset, left: pos.left + pos.width / 2 - actualWidth / 2}
            break
          case 'left':
            tp = {top: pos.top + pos.height / 2 - actualHeight / 2, left: pos.left - actualWidth - this.options.offset}
            break
          case 'right':
            tp = {top: pos.top + pos.height / 2 - actualHeight / 2, left: pos.left + pos.width + this.options.offset}
            break
        }

        $tip
          .css(tp)
          .addClass(placement)
          .addClass('in')
      }
    }

  , setContent: function () {
      var $tip = this.tip()
      $tip.find('.twipsy-inner')[this.options.html ? 'html' : 'text'](this.getTitle())
      $tip[0].className = 'twipsy'
    }

  , hide: function() {
      var that = this
        , $tip = this.tip()

      $tip.removeClass('in')

      function removeElement () {
        $tip.remove()
      }

      $.support.transition && this.$tip.hasClass('fade') ?
        $tip.bind(transitionEnd, removeElement) :
        removeElement()
        
      removeElement();
    }

  , fixTitle: function() {
      var $e = this.$element
      if ($e.attr('title') || typeof($e.attr('data-original-title')) != 'string') {
        $e.attr('data-original-title', $e.attr('title') || '').removeAttr('title')
      }
    }

  , getTitle: function() {
      var title
        , $e = this.$element
        , o = this.options

        this.fixTitle()

        if (typeof o.title == 'string') {
          title = $e.attr(o.title == 'title' ? 'data-original-title' : o.title)
        } else if (typeof o.title == 'function') {
          title = o.title.call($e[0])
        }

        title = ('' + title).replace(/(^\s*|\s*$)/, "")

        return title || o.fallback
    }

  , tip: function() {
      if (!this.$tip) {
        this.$tip = $('<div class="twipsy" />').html('<div class="twipsy-arrow"></div><div class="twipsy-inner"></div>')
      }
      return this.$tip
    }

  , validate: function() {
      if (!this.$element[0].parentNode) {
        this.hide()
        this.$element = null
        this.options = null
      }
    }

  , enable: function() {
      this.enabled = true
    }

  , disable: function() {
      this.enabled = false
    }

  , toggleEnabled: function() {
      this.enabled = !this.enabled
    }

  }


 /* TWIPSY PRIVATE METHODS
  * ====================== */

   var _ = {

     maybeCall: function ( thing, ctx ) {
       return (typeof thing == 'function') ? (thing.call(ctx)) : thing
     }

   }


 /* TWIPSY PLUGIN DEFINITION
  * ======================== */

  $.fn.twipsy = function (options) {
    $.fn.twipsy.initWith.call(this, options, Twipsy, 'twipsy')
    return this
  }

  $.fn.twipsy.initWith = function (options, Constructor, name) {
    var twipsy
      , binder
      , eventIn
      , eventOut

    if (options === true) {
      return this.data(name)
    } else if (typeof options == 'string') {
      twipsy = this.data(name)
      if (twipsy) {
        twipsy[options]()
      }
      return this
    }

    options = $.extend({}, $.fn[name].defaults, options)

    function get(ele) {
      var twipsy = $.data(ele, name)

      if (!twipsy) {
        twipsy = new Constructor(ele, $.fn.twipsy.elementOptions(ele, options))
        $.data(ele, name, twipsy)
      }

      return twipsy
    }

    function enter() {
      var twipsy = get(this)
      twipsy.hoverState = 'in'

      if (options.delayIn == 0) {
        twipsy.show()
      } else {
        twipsy.fixTitle()
        setTimeout(function() {
          if (twipsy.hoverState == 'in') {
            twipsy.show()
          }
        }, options.delayIn)
      }
    }

    function leave() {
      var twipsy = get(this)
      twipsy.hoverState = 'out'
      if (options.delayOut == 0) {
        twipsy.hide()
      } else {
        setTimeout(function() {
          if (twipsy.hoverState == 'out') {
            twipsy.hide()
          }
        }, options.delayOut)
      }
    }

    if (!options.live) {
      this.each(function() {
        get(this)
      })
    }

    if (options.trigger != 'manual') {
      binder   = options.live ? 'live' : 'bind'
      eventIn  = options.trigger == 'hover' ? 'mouseenter' : 'focus'
      eventOut = options.trigger == 'hover' ? 'mouseleave' : 'blur'
      this[binder](eventIn, enter)[binder](eventOut, leave)
    }

    return this
  }

  $.fn.twipsy.Twipsy = Twipsy

  $.fn.twipsy.defaults = {
    animate: true
  , delayIn: 0
  , delayOut: 0
  , fallback: ''
  , placement: 'above'
  , html: false
  , live: false
  , offset: 0
  , title: 'title'
  , trigger: 'hover'
  }

  $.fn.twipsy.elementOptions = function(ele, options) {
    return $.metadata ? $.extend({}, options, $(ele).metadata()) : options
  }

}( window.jQuery || window.ender );
