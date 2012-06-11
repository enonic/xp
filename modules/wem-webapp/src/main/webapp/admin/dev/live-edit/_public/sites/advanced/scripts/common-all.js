$(function() {

    // Strict fix for target = _blank
    $('a[rel$="external"]').click(function(){
        this.target = '_blank';
    });
    
    // Accessibility contrast
    if ($.cookie('high_contrast')) $('body').addClass('contrast');
    $('#contrast').click(function(){
        if ($.cookie('high_contrast')) {
            $('body').removeClass('contrast');
            $.cookie('high_contrast', null, {path: '/'});
        } else {
            $('body').addClass('contrast');
            $.cookie('high_contrast', 'true', {path: '/'});
        }
    });
    
    // Accessibility text size
    if ($.cookie('text_size')) $('body').addClass('text-size-' + $.cookie('text_size'));
    $('#text-size > a').click(function(){
        if ($(this).hasClass('large-text') || ($(this).hasClass('change-text-size') && !$('body').hasClass('text-size-large') && !$('body').hasClass('text-size-largest'))) {
            $('body').removeClass('text-size-largest').addClass('text-size-large');
            $.cookie('text_size', 'large', {path: '/'});
        } else if ($(this).hasClass('largest-text') || ($(this).hasClass('change-text-size') && $('body').hasClass('text-size-large'))) {
            $('body').removeClass('text-size-large').addClass('text-size-largest');
            $.cookie('text_size', 'largest', {path: '/'});
        } else {
            $('body').removeClass('text-size-large').removeClass('text-size-largest');
            $.cookie('text_size', null, {path: '/'});
        }
    });
    
    // Adds required * to all required form elements
    $('.required').not('span').each(function () {
        $('label[for = '+this.id+']').not('.radio').append('<span class="required">*</span>');
    });
    
    // Initializes tabs and follows href instead of selecting tab if complete url
    $('.tabs').tabs({
        select: function(event, ui) {
            var url = $.data(ui.tab, 'load.tabs');
            if( url ) {
                location.href = url;
                return false;
            }
            return true;
        }
    });
    
    // File archive
    $('#file-archive > ul').find('.column').hover(
        function () {
            $(this).siblings('.column').andSelf().addClass("highlight");
        },
        function () {
            $(this).siblings('.column').andSelf().removeClass("highlight");
        }
    );
    
    $('#file-archive > ul.menu').find('.column').click(function () {
        if ($(this).parent().hasClass('folder')) {
            if ($(this).nextAll('ul').length > 0) {
                $(this).nextAll('ul').slideToggle('fast', function() {
                    changeIcon($(this));
                });
            } else {
                changeIcon($(this));
            }
        } else {
            window.location = $(this).parent().find('a').attr('href');
        }
    });
    
    $('input.button.submit').each(function () {
        var replacement = $('<a><span>' + this.value + '</span></a>').attr('href', '#').addClass('button submit').click(function(e) {
            $(this).closest('form').submit();
            e.preventDefault();
        });
        $(this).replaceWith(replacement);
    });
    
    
    
    $('.enonic-table-1 tr:even').addClass('even');
    $('.enonic-table-1 tr:odd').addClass('odd');
    $('.enonic-table-1 td').hover(function(e) {
        
        var col = $(this).parent().children().index($(this)) + 1;        
        
        $(this).closest('table').find('td:nth-child(' + col + ')').addClass('hover');
        $(this).closest('tr').find('td').addClass('hover');
        
    }, function(e) {
    
        var col = $(this).parent().children().index($(this)) + 1;        
        
        $(this).closest('table').find('td:nth-child(' + col + ')').removeClass('hover');
        $(this).closest('tr').find('td').removeClass('hover');
    });
    
    // Salesforce integration with formbuilder
    $(".salesforce-submit").submit(function(e) {
    	//console.log('The salesforce submit is being processed!');
        var trialRequested = 0;
        if ($("#formbuilder-form").hasClass("salesforce-trial")) {trialRequested = 1;}
        var name = "", email = "", phone = "", company = "", title = "", description = "";
        // Retrieve values from either Trial or Contact form
        // NOTE: Editing the formbuilder inputs could break this script
        if (trialRequested == 1) {
            name = $("#form_1932_elm_1").val();
            email = $("#form_1932_elm_2").val();
            company = $("#form_1932_elm_3").val();
            description = $("#form_1932_elm_4").val();
        } else {
            name = $("#form_1936_elm_1").val();
            email = $("#form_1936_elm_2").val();
            phone = $("#form_1936_elm_3").val();
            company = $("#form_1936_elm_4").val();
            title = $("#form_1936_elm_5").val();
            description = $("#form_1936_elm_6").val();
        }
        // Serialize and send in the data
        var salesforceData = {
            "oid": "00D20000000JPzC",
            "retURL": "http://www.enonic.com",
            "lead_source": "Web",
            "00N200000031GbW": trialRequested,
            "first_name": name,
            "last_name": name,
            "email": email,
            "phone": phone,
            "company": company,
            "title": title,
            "description": description
        }
        var serializedData = $.param(salesforceData);
        $.ajax({
            type: "POST",
            url: "https://www.salesforce.com/servlet/servlet.WebToLead?encoding=UTF-8",
            data: serializedData
        });
        console.log('Salesforce!');
    });
    
});

// Used by file archive
function changeIcon(elm) {
    var icon = elm.parent().children('.name').find('img');
    if (icon.attr('src').search(/icon-folder-open./) != -1) {
        icon.attr('src', icon.attr('src').replace(/icon-folder-open./, 'icon-folder.'));
    } else {
        icon.attr('src', icon.attr('src').replace(/icon-folder./, 'icon-folder-open.'));
    }
}

// Reloads captcha image
function reloadCaptcha(imageId) {
    var src = document.getElementById(imageId).src;
    document.getElementById(imageId).src = src.split('?')[0] + '?' + (new Date()).getTime();
}


/*
	Oracle Deployment Toolkit Script
	http://download.oracle.com/javase/6/docs/technotes/guides/jweb/deployment_advice.html#deplToolkit
*/
var deployJava={debug:null,firefoxJavaVersion:null,myInterval:null,preInstallJREList:null,returnPage:null,brand:null,locale:null,installType:null,EAInstallEnabled:false,EarlyAccessURL:null,getJavaURL:'http://java.sun.com/webapps/getjava/BrowserRedirect?host=java.com',appleRedirectPage:'http://www.apple.com/support/downloads/',oldMimeType:'application/npruntime-scriptable-plugin;DeploymentToolkit',mimeType:'application/java-deployment-toolkit',launchButtonPNG:'http://java.sun.com/products/jfc/tsc/articles/swing2d/webstart.png',browserName:null,browserName2:null,getJREs:function(){var list=new Array();if(deployJava.isPluginInstalled()){var plugin=deployJava.getPlugin();var VMs=plugin.jvms;for(var i=0;i<VMs.getLength();i++){list[i]=VMs.get(i).version;}}else{var browser=deployJava.getBrowser();if(browser=='MSIE'){if(deployJava.testUsingActiveX('1.7.0')){list[0]='1.7.0';}else if(deployJava.testUsingActiveX('1.6.0')){list[0]='1.6.0';}else if(deployJava.testUsingActiveX('1.5.0')){list[0]='1.5.0';}else if(deployJava.testUsingActiveX('1.4.2')){list[0]='1.4.2';}else if(deployJava.testForMSVM()){list[0]='1.1';}}else if(browser=='Netscape Family'){deployJava.getJPIVersionUsingMimeType();if(deployJava.firefoxJavaVersion!=null){list[0]=deployJava.firefoxJavaVersion;}else if(deployJava.testUsingMimeTypes('1.7')){list[0]='1.7.0';}else if(deployJava.testUsingMimeTypes('1.6')){list[0]='1.6.0';}else if(deployJava.testUsingMimeTypes('1.5')){list[0]='1.5.0';}else if(deployJava.testUsingMimeTypes('1.4.2')){list[0]='1.4.2';}else if(deployJava.browserName2=='Safari'){if(deployJava.testUsingPluginsArray('1.7.0')){list[0]='1.7.0';}else if(deployJava.testUsingPluginsArray('1.6')){list[0]='1.6.0';}else if(deployJava.testUsingPluginsArray('1.5')){list[0]='1.5.0';}else if(deployJava.testUsingPluginsArray('1.4.2')){list[0]='1.4.2';}}}}
if(deployJava.debug){for(var i=0;i<list.length;++i){alert('We claim to have detected Java SE '+list[i]);}}
return list;},installJRE:function(requestVersion){var ret=false;if(deployJava.isPluginInstalled()){if(deployJava.getPlugin().installJRE(requestVersion)){deployJava.refresh();if(deployJava.returnPage!=null){document.location=deployJava.returnPage;}
return true;}else{return false;}}else{return deployJava.installLatestJRE();}},installLatestJRE:function(){if(deployJava.isPluginInstalled()){if(deployJava.getPlugin().installLatestJRE()){deployJava.refresh();if(deployJava.returnPage!=null){document.location=deployJava.returnPage;}
return true;}else{return false;}}else{var browser=deployJava.getBrowser();var platform=navigator.platform.toLowerCase();if((deployJava.EAInstallEnabled=='true')&&(platform.indexOf('win')!=-1)&&(deployJava.EarlyAccessURL!=null)){deployJava.preInstallJREList=deployJava.getJREs();if(deployJava.returnPage!=null){deployJava.myInterval=setInterval("deployJava.poll()",3000);}
location.href=deployJava.EarlyAccessURL;return false;}else{if(browser=='MSIE'){return deployJava.IEInstall();}else if((browser=='Netscape Family')&&(platform.indexOf('win32')!=-1)){return deployJava.FFInstall();}else{location.href=deployJava.getJavaURL+
((deployJava.returnPage!=null)?('&returnPage='+deployJava.returnPage):'')+
((deployJava.locale!=null)?('&locale='+deployJava.locale):'')+
((deployJava.brand!=null)?('&brand='+deployJava.brand):'');}
return false;}}},runApplet:function(attributes,parameters,minimumVersion){if(minimumVersion=='undefined'||minimumVersion==null){minimumVersion='1.1';}
var regex="^(\\d+)(?:\\.(\\d+)(?:\\.(\\d+)(?:_(\\d+))?)?)?$";var matchData=minimumVersion.match(regex);if(deployJava.returnPage==null){deployJava.returnPage=document.location;}
if(matchData!=null){var browser=deployJava.getBrowser();if((browser!='?')&&('Safari'!=deployJava.browserName2)){if(deployJava.versionCheck(minimumVersion+'+')){deployJava.writeAppletTag(attributes,parameters);}else if(deployJava.installJRE(minimumVersion+'+')){deployJava.refresh();location.href=document.location;deployJava.writeAppletTag(attributes,parameters);}}else{deployJava.writeAppletTag(attributes,parameters);}}else{if(deployJava.debug){alert('Invalid minimumVersion argument to runApplet():'+
minimumVersion);}}},writeAppletTag:function(attributes,parameters){var startApplet='<'+'applet ';var params='';var endApplet='<'+'/'+'applet'+'>';var addCodeAttribute=true;for(var attribute in attributes){startApplet+=(' '+attribute+'="'+attributes[attribute]+'"');if(attribute=='code'||attribute=='java_code'){addCodeAttribute=false;}}
if(parameters!='undefined'&&parameters!=null){var codebaseParam=false;for(var parameter in parameters){if(parameter=='codebase_lookup'){codebaseParam=true;}
if(parameter=='object'||parameter=='java_object'){addCodeAttribute=false;}
params+='<param name="'+parameter+'" value="'+
parameters[parameter]+'"/>';}
if(!codebaseParam){params+='<param name="codebase_lookup" value="false"/>';}}
if(addCodeAttribute){startApplet+=(' code="dummy"');}
startApplet+='>';document.write(startApplet+'\n'+params+'\n'+endApplet);},versionCheck:function(versionPattern)
{var index=0;var regex="^(\\d+)(?:\\.(\\d+)(?:\\.(\\d+)(?:_(\\d+))?)?)?(\\*|\\+)?$";var matchData=versionPattern.match(regex);if(matchData!=null){var familyMatch=true;var patternArray=new Array();for(var i=1;i<matchData.length;++i){if((typeof matchData[i]=='string')&&(matchData[i]!='')){patternArray[index]=matchData[i];index++;}}
if(patternArray[patternArray.length-1]=='+'){familyMatch=false;patternArray.length--;}else{if(patternArray[patternArray.length-1]=='*'){patternArray.length--;}}
var list=deployJava.getJREs();for(var i=0;i<list.length;++i){if(deployJava.compareVersionToPattern(list[i],patternArray,familyMatch)){return true;}}
return false;}else{alert('Invalid versionPattern passed to versionCheck: '+
versionPattern);return false;}},isWebStartInstalled:function(minimumVersion){var browser=deployJava.getBrowser();if((browser=='?')||('Safari'==deployJava.browserName2)){return true;}
if(minimumVersion=='undefined'||minimumVersion==null){minimumVersion='1.4.2';}
var retval=false;var regex="^(\\d+)(?:\\.(\\d+)(?:\\.(\\d+)(?:_(\\d+))?)?)?$";var matchData=minimumVersion.match(regex);if(matchData!=null){retval=deployJava.versionCheck(minimumVersion+'+');}else{if(deployJava.debug){alert('Invalid minimumVersion argument to isWebStartInstalled(): '+minimumVersion);}
retval=deployJava.versionCheck('1.4.2+');}
return retval;},getJPIVersionUsingMimeType:function(){for(var i=0;i<navigator.mimeTypes.length;++i){var s=navigator.mimeTypes[i].type;var m=s.match(/^application\/x-java-applet;jpi-version=(.*)$/);if(m!=null){deployJava.firefoxJavaVersion=m[1];if('Opera'!=deployJava.browserName2){break;}}}},launchWebStartApplication:function(jnlp){var uaString=navigator.userAgent.toLowerCase();deployJava.getJPIVersionUsingMimeType();if(deployJava.isWebStartInstalled('1.7.0')==false){if((deployJava.installJRE('1.7.0+')==false)||((deployJava.isWebStartInstalled('1.7.0')==false))){return false;}}
var jnlpDocbase=null;if(document.documentURI){jnlpDocbase=document.documentURI;}
if(jnlpDocbase==null){jnlpDocbase=document.URL;}
var browser=deployJava.getBrowser();var launchTag;if(browser=='MSIE'){launchTag='<'+'object classid="clsid:8AD9C840-044E-11D1-B3E9-00805F499D93" '+'width="0" height="0">'+'<'+'PARAM name="launchjnlp" value="'+jnlp+'"'+'>'+'<'+'PARAM name="docbase" value="'+jnlpDocbase+'"'+'>'+'<'+'/'+'object'+'>';}else if(browser=='Netscape Family'){launchTag='<'+'embed type="application/x-java-applet;jpi-version='+
deployJava.firefoxJavaVersion+'" '+'width="0" height="0" '+'launchjnlp="'+jnlp+'"'+'docbase="'+jnlpDocbase+'"'+' />';}
if(document.body=='undefined'||document.body==null){document.write(launchTag);document.location=jnlpDocbase;}else{var divTag=document.createElement("div");divTag.id="div1";divTag.style.position="relative";divTag.style.left="-10000px";divTag.style.margin="0px auto";divTag.className="dynamicDiv";divTag.innerHTML=launchTag;document.body.appendChild(divTag);}},createWebStartLaunchButtonEx:function(jnlp,minimumVersion){if(deployJava.returnPage==null){deployJava.returnPage=jnlp;}
var url='javascript:deployJava.launchWebStartApplication(\''+jnlp+'\');';document.write('<'+'a href="'+url+'" onMouseOver="window.status=\'\'; '+'return true;"><'+'img '+'src="'+deployJava.launchButtonPNG+'" '+'border="0" /><'+'/'+'a'+'>');},createWebStartLaunchButton:function(jnlp,minimumVersion){if(deployJava.returnPage==null){deployJava.returnPage=jnlp;}
var url='javascript:'+'if (!deployJava.isWebStartInstalled(&quot;'+
minimumVersion+'&quot;)) {'+'if (deployJava.installLatestJRE()) {'+'if (deployJava.launch(&quot;'+jnlp+'&quot;)) {}'+'}'+'} else {'+'if (deployJava.launch(&quot;'+jnlp+'&quot;)) {}'+'}';document.write('<'+'a href="'+url+'" onMouseOver="window.status=\'\'; '+'return true;"><'+'img '+'src="'+deployJava.launchButtonPNG+'" '+'border="0" /><'+'/'+'a'+'>');},launch:function(jnlp){document.location=jnlp;return true;},isPluginInstalled:function(){var plugin=deployJava.getPlugin();if(plugin&&plugin.jvms){return true;}else{return false;}},isAutoUpdateEnabled:function(){if(deployJava.isPluginInstalled()){return deployJava.getPlugin().isAutoUpdateEnabled();}
return false;},setAutoUpdateEnabled:function(){if(deployJava.isPluginInstalled()){return deployJava.getPlugin().setAutoUpdateEnabled();}
return false;},setInstallerType:function(type){deployJava.installType=type;if(deployJava.isPluginInstalled()){return deployJava.getPlugin().setInstallerType(type);}
return false;},setAdditionalPackages:function(packageList){if(deployJava.isPluginInstalled()){return deployJava.getPlugin().setAdditionalPackages(packageList);}
return false;},setEarlyAccess:function(enabled){deployJava.EAInstallEnabled=enabled;},isPlugin2:function(){if(deployJava.isPluginInstalled()){if(deployJava.versionCheck('1.6.0_10+')){try{return deployJava.getPlugin().isPlugin2();}catch(err){}}}
return false;},allowPlugin:function(){deployJava.getBrowser();var ret=('Safari'!=deployJava.browserName2&&'Opera'!=deployJava.browserName2);return ret;},getPlugin:function(){deployJava.refresh();var ret=null;if(deployJava.allowPlugin()){ret=document.getElementById('deployJavaPlugin');}
return ret;},compareVersionToPattern:function(version,patternArray,familyMatch){var regex="^(\\d+)(?:\\.(\\d+)(?:\\.(\\d+)(?:_(\\d+))?)?)?$";var matchData=version.match(regex);if(matchData!=null){var index=0;var result=new Array();for(var i=1;i<matchData.length;++i){if((typeof matchData[i]=='string')&&(matchData[i]!=''))
{result[index]=matchData[i];index++;}}
var l=Math.min(result.length,patternArray.length);if(familyMatch){for(var i=0;i<l;++i){if(result[i]!=patternArray[i])return false;}
return true;}else{for(var i=0;i<l;++i){if(result[i]<patternArray[i]){return false;}else if(result[i]>patternArray[i]){return true;}}
return true;}}else{return false;}},getBrowser:function(){if(deployJava.browserName==null){var browser=navigator.userAgent.toLowerCase();if(deployJava.debug){alert('userAgent -> '+browser);}
if(browser.indexOf('msie')!=-1){deployJava.browserName='MSIE';deployJava.browserName2='MSIE';}else if(browser.indexOf('iphone')!=-1){deployJava.browserName='Netscape Family';deployJava.browserName2='iPhone';}else if(browser.indexOf('firefox')!=-1){deployJava.browserName='Netscape Family';deployJava.browserName2='Firefox';}else if(browser.indexOf('chrome')!=-1){deployJava.browserName='Netscape Family';deployJava.browserName2='Chrome';}else if(browser.indexOf('safari')!=-1){deployJava.browserName='Netscape Family';deployJava.browserName2='Safari';}else if(browser.indexOf('mozilla')!=-1){deployJava.browserName='Netscape Family';deployJava.browserName2='Other';}else if(browser.indexOf('opera')!=-1){deployJava.browserName='Netscape Family';deployJava.browserName2='Opera';}else{deployJava.browserName='?';deployJava.browserName2='unknown';}
if(deployJava.debug){alert('Detected browser name:'+deployJava.browserName+', '+deployJava.browserName2);}}
return deployJava.browserName;},testUsingActiveX:function(version){var objectName='JavaWebStart.isInstalled.'+version+'.0';if(!ActiveXObject){if(deployJava.debug){alert('Browser claims to be IE, but no ActiveXObject object?');}
return false;}
try{return(new ActiveXObject(objectName)!=null);}catch(exception){return false;}},testForMSVM:function(){var clsid='{08B0E5C0-4FCB-11CF-AAA5-00401C608500}';if(typeof oClientCaps!='undefined'){var v=oClientCaps.getComponentVersion(clsid,"ComponentID");if((v=='')||(v=='5,0,5000,0')){return false;}else{return true;}}else{return false;}},testUsingMimeTypes:function(version){if(!navigator.mimeTypes){if(deployJava.debug){alert('Browser claims to be Netscape family, but no mimeTypes[] array?');}
return false;}
for(var i=0;i<navigator.mimeTypes.length;++i){s=navigator.mimeTypes[i].type;var m=s.match(/^application\/x-java-applet\x3Bversion=(1\.8|1\.7|1\.6|1\.5|1\.4\.2)$/);if(m!=null){if(deployJava.compareVersions(m[1],version)){return true;}}}
return false;},testUsingPluginsArray:function(version){if((!navigator.plugins)||(!navigator.plugins.length)){return false;}
var platform=navigator.platform.toLowerCase();for(var i=0;i<navigator.plugins.length;++i){s=navigator.plugins[i].description;if(s.search(/^Java Switchable Plug-in (Cocoa)/)!=-1){if(deployJava.compareVersions("1.5.0",version)){return true;}}else if(s.search(/^Java/)!=-1){if(platform.indexOf('win')!=-1){if(deployJava.compareVersions("1.5.0",version)||deployJava.compareVersions("1.6.0",version)){return true;}}}}
if(deployJava.compareVersions("1.5.0",version)){return true;}
return false;},IEInstall:function(){location.href=deployJava.getJavaURL+
((deployJava.returnPage!=null)?('&returnPage='+deployJava.returnPage):'')+
((deployJava.locale!=null)?('&locale='+deployJava.locale):'')+
((deployJava.brand!=null)?('&brand='+deployJava.brand):'')+
((deployJava.installType!=null)?('&type='+deployJava.installType):'');return false;},done:function(name,result){},FFInstall:function(){location.href=deployJava.getJavaURL+
((deployJava.returnPage!=null)?('&returnPage='+deployJava.returnPage):'')+
((deployJava.locale!=null)?('&locale='+deployJava.locale):'')+
((deployJava.brand!=null)?('&brand='+deployJava.brand):'')+
((deployJava.installType!=null)?('&type='+deployJava.installType):'');return false;},compareVersions:function(installed,required){var a=installed.split('.');var b=required.split('.');for(var i=0;i<a.length;++i){a[i]=Number(a[i]);}
for(var i=0;i<b.length;++i){b[i]=Number(b[i]);}
if(a.length==2){a[2]=0;}
if(a[0]>b[0])return true;if(a[0]<b[0])return false;if(a[1]>b[1])return true;if(a[1]<b[1])return false;if(a[2]>b[2])return true;if(a[2]<b[2])return false;return true;},enableAlerts:function(){deployJava.browserName=null;deployJava.debug=true;},poll:function(){deployJava.refresh();var postInstallJREList=deployJava.getJREs();if((deployJava.preInstallJREList.length==0)&&(postInstallJREList.length!=0)){clearInterval(deployJava.myInterval);if(deployJava.returnPage!=null){location.href=deployJava.returnPage;};}
if((deployJava.preInstallJREList.length!=0)&&(postInstallJREList.length!=0)&&(deployJava.preInstallJREList[0]!=postInstallJREList[0])){clearInterval(deployJava.myInterval);if(deployJava.returnPage!=null){location.href=deployJava.returnPage;}}},writePluginTag:function(){var browser=deployJava.getBrowser();if(browser=='MSIE'){document.write('<'+'object classid="clsid:CAFEEFAC-DEC7-0000-0000-ABCDEFFEDCBA" '+'id="deployJavaPlugin" width="0" height="0">'+'<'+'/'+'object'+'>');}else if(browser=='Netscape Family'&&deployJava.allowPlugin()){deployJava.writeEmbedTag();}},refresh:function(){navigator.plugins.refresh(false);var browser=deployJava.getBrowser();if(browser=='Netscape Family'&&deployJava.allowPlugin()){var plugin=document.getElementById('deployJavaPlugin');if(plugin==null){deployJava.writeEmbedTag();}}},writeEmbedTag:function(){var written=false;if(navigator.mimeTypes!=null){for(var i=0;i<navigator.mimeTypes.length;i++){if(navigator.mimeTypes[i].type==deployJava.mimeType){if(navigator.mimeTypes[i].enabledPlugin){document.write('<'+'embed id="deployJavaPlugin" type="'+
deployJava.mimeType+'" hidden="true" />');written=true;}}}
if(!written)for(var i=0;i<navigator.mimeTypes.length;i++){if(navigator.mimeTypes[i].type==deployJava.oldMimeType){if(navigator.mimeTypes[i].enabledPlugin){document.write('<'+'embed id="deployJavaPlugin" type="'+
deployJava.oldMimeType+'" hidden="true" />');}}}}},do_initialize:function(){deployJava.writePluginTag();if(deployJava.locale==null){var loc=null;if(loc==null)try{loc=navigator.userLanguage;}catch(err){}
if(loc==null)try{loc=navigator.systemLanguage;}catch(err){}
if(loc==null)try{loc=navigator.language;}catch(err){}
if(loc!=null){loc.replace("-","_")
deployJava.locale=loc;}}}};deployJava.do_initialize();
