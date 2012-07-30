var imgbase = "./images/";
var _initArticle = "";

$(document).ready(function(){

    //Ligthbox
    $("a[rel^='lightbox-iframe']").click(function(e) {
       e.preventDefault();
       var url = $(this).attr('href');
       var height = getValueFromUrlParameter(url, 'height');
		if (!height) { height = 800;}
       var width = getValueFromUrlParameter(url, 'width');
		if (!width) { width = 640; }
       $.openDOMWindow({
        height:height, 
        width:width, 
        windowSource:'iframe', 
        windowPadding:0,
        windowSourceURL: url
    });
   });
   
   illustrationLightbox();

    // Font size
    $(document.body).addClass(readCookie("fontsize"));
    $("#globalmenu #fontsize").bind("click",function(){
        var docbody = $(document.body);
        docbody.toggleClass("bigger");
        writeCookie( "fontsize", (docbody.hasClass("bigger") == true) ? 'bigger' : '', 30);
        return false;
    }
            );

    // Hide consents
    $(".consentcollapsed ul").hide();

    // Ajax enable product pages
    if ($(document.body).is(":has(#productmenu)")) {

        // Save main content
        _initArticle = $("#article").html();

        //$.historyInit(pageload);
        $.historyInit(pageload);

        // set onlick event for buttons
        $("#productmenu a").click(function(){
            var hash = this.id.substring(1);
            if (hash != '') {
                $.historyLoad(hash);
                return false;
            }
        });

    }

    // Add toggle to components
    $("#sidebar1 .component").each(function(){

        var comp = $(this);
        var compheader = comp.find("h2");
        var compid = comp.attr("id");

        // Add toggle to all components except logout
        if ( ! ( compid == "login" && comp.is(":has(form[@name = 'logout'])") ) ) {

            var state = readCookie("comp-" + compid);
            if (state == undefined && comp.hasClass("defaultOpen") ) {
                state = 1;
            }

            // Add arrow and set open state
            compheader.addClass("open");

            // Add toggle onclick
            compheader.bind("click", function(){ toggleComponent(comp.attr("id")); });

            // Keep component open if the user has opened it in this session
            if (state == "1") {}

            // Close component open if the user has closed it in this session
            else if (state == "0") { hideComponent(compid); }

            // Keep login open on frontpage
            else if (compid == "login" && $("body").hasClass("frontpage")) {}

            // Keep contactus open an all other pages than frontpage
            else if ((compid == "callme" || compid == "contact-us") && !$("body").hasClass("frontpage")) {}

            // Keep components open on Din side
            else if ( $("body").hasClass("dinside") ) {}

            // Hide the rest
            else hideComponent(compid);

        }

    });


    // Add popup to onlineforms
    $("a.onlineform").bind("click", function(){
        return openWin(this.href,'Gjensidige', 800, 800);
    });

    // Add ajax to callme
    $(".callmeform").submit( function() {
        var callmeform = $(this);

        // get url
        var ajaxurl = callmeform.find("#callmeurl").attr("href") + "?" + callmeform.serialize();

        // remove previous feedback 
        $(".callmeform ~ *").each( function() {
            $(this).remove();
        });

        // add wair
        var wait = $("<p class='wait'>Laster...</p>");
        callmeform.after(wait);

        // do ajax
        $.get(ajaxurl, function(data){

            // remove wait
            wait.remove();

            // show feedback
            $(data).find(".feedback").each( function() {
                callmeform.after($(this));
            });

        });

        return false;

    });

    // Add ssn when user clikcs new user
    $("#sidebar1 #login #newuser").bind("click", function(){
        this.href = this.href + "?personInfoFoedselsnummer=" + $("#sidebar1 #login input#user").val();
    });

    // Focus on login
    $("input#user").focus();

    $("#sidebar1 #login form").submit(function(){
        var uid = $(this).find("input#user").val();
        if ( uid == '' ) {
            if ( $(this).is(":has(#loginerror)")  ) {
                $("#loginerror").hide();
                $("#loginerror").addClass("error");
                $("#loginerror").fadeIn();
            } else {
                $(this).prepend("<p id='loginerror' class='feedback warning'>Vennligst tast inn et 11-sifret fødselsnummer</p>").fadeIn();
            }
            $(this).find("input#user").focus();
            return false;
        } else {
            return true;
        }
    }
            );

    // Focus on first content input
    $("#content :text:first").focus();

    ReportArchive.init();
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
        newwindow=window.open(url,title,'menubar=1,status=1,location=0,toolbar=0,scrollbars=1,resizable=1,height='+hoyde+',width='+bredde+',left='+left+',top='+top+'');
        if (!newwindow.opener) newwindow.opener = self;
    }
    if (window.focus) {
        newwindow.focus()
    }
    return false;
}

function illustrationLightbox() {
    $('.illustrationbox').live('click', function(e) {
            e.preventDefault();
            var url = $(this).attr('href');
            var height = getValueFromUrlParameter(url, 'height');
			if (!height) { height = 800;}
            var width = getValueFromUrlParameter(url, 'width');
			if (!width) { width = 640; }
            $.openDOMWindow({
             height:height, 
             width:width, 
             windowSource:'iframe', 
             windowSourceURL: url
         });
     });
}
function getValueFromUrlParameter(url, name) {
    name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
    var regexS = "[\\?&]"+name+"=([^&#]*)";
    var regex = new RegExp( regexS );
    var results = regex.exec( url );
    if( results == null )
       return "";
    else
       return results[1];
}
function getAjaxProductUrl(id) {
    var ajaxurl = null;
    if ( !isNaN(id) ) {
        var ajaxurl = "ajax/article-productpage?pid=" + id;
        /*ajaxurl = "article-productpage.htm?pid=" + id;
         if (id == "215") { ajaxurl = "article-productpage2.htm?"; }
         if (id == "238") ajaxurl = "finnes-ikke.htm?"; */
    }
    return ajaxurl;
}


function pageload(hash) {
    if (hash) {
        var ajaxurl = getAjaxProductUrl(hash);
        if (ajaxurl != null) {
            var id = "p" + hash;
            $.ajax({
                type: "GET",
                url: ajaxurl,
                timeout: 5000,
                success: function(html){
                    // console.log("success");
                    $("#article").html( html );
                    $("#productmenu li.selected").removeClass("selected");
                    $("#productmenu li:has(#" + id + "):last").addClass("selected");
                    $("li:has(#" + id + "):last").addClass("selected");
                    document.title = $("#article h2").text() + (" - Gjensidige")
                },
                error: function(msg){
                    //console.log("error");
                    document.location = $("#" + id).attr("href");
                }
            });
        }
    }  else {
        // $("#productmenu li.selected").removeClass("selected");
        $("#article").html( _initArticle );
    }
}


function toggleComponent(componentid){

    var comp = $("#" + componentid);
    var content = comp.find(".wrapper");
    var compheader = comp.find("h2");

    // If visible
    if (compheader.hasClass("open")) {
        content.slideUp("slow");
        compheader.toggleClass("collapsed");
        compheader.toggleClass("open");
        writeCookie("comp-" + componentid, "0", 1);
    } else {
        content.slideDown("slow");
        compheader.toggleClass("collapsed");
        compheader.toggleClass("open");
        writeCookie("comp-" + componentid, "1", 1);
    }
}

function hideComponent(componentid) {
    var comp = $("#" + componentid);
    var content = comp.find(".wrapper");
    var compheader = comp.find("h2");
    compheader.toggleClass("collapsed");
    compheader.toggleClass("open");
    comp.find(".wrapper").hide()
}

function writeCookie(name,value,days) {
    if (days) {
        var date = new Date();
        date.setTime(date.getTime()+(days*24*60*60*1000));
        var expires = "; expires="+date.toGMTString();
    }
    else var expires = "";
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
    writeCookie(name,"",-1);
}

function toggleConsentGroup(box, fieldsetId) {
    var theForm = document.samtykkeForm;
    var i;
    for (i=0; i<theForm.length; i++) {
        if ( theForm.elements[i].value.indexOf("consent" + fieldsetId + "_") == 0 ) {
            theForm.elements[i].checked = box.checked;
        }
        if (theForm.noconsent && theForm.elements[i].checked == true) {
            theForm.noconsent.checked = false;
        }
    }
}

function toggleParentGroup(fieldsetId) {
    var theForm = document.samtykkeForm;
    var i;
    var groupChecked = true;
    for (i=0; i<theForm.length; i++) {
        if ( theForm.elements[i].value.indexOf("consent" + fieldsetId + "_") == 0 ) {
            if (theForm.elements[i].checked == false) {
                groupChecked = false;
            } else if (theForm.noconsent) {
                theForm.noconsent.checked = false;
            }
        }
    }
    for (i=0; i<theForm.length; i++) {
        if ( theForm.elements[i].value == fieldsetId && theForm.elements[i].name == 'group'  ) {
            theForm.elements[i].checked = groupChecked;
        }
    }
}

function noConsent(elem) {
    var theForm = document.samtykkeForm;
    var i;
    if (elem.checked) {
        for (i=0; i<theForm.length; i++) {
            if ( theForm.elements[i].type == 'checkbox' && theForm.elements[i].name != 'noconsent' ) {
                theForm.elements[i].checked = false;
            }
        }
    }
}

function reloadRisiki() {
    return true;
}


var CisionSubscription = (function(){

    var form;
    var isFormOk;

    function setFormByChild(childElement) {
        form = $(childElement).parents("form");
    }

    function setSubscribeAction(subscribeAction) {
        form.find("input[name='wpySubscribeAction']")[0].value = subscribeAction;
    }

    function doValidateForm() {
        isFormOk = true;

        validateSubscribeAction();

        if (!isFormOk) {
            //stop validating if subscriptionAction is blank
            return false;
        }

        hideServerError();

        validateReleaseType();
        validateLanguage();
        validateContactInfo();

        toggleErrorMessageContainer();

        return isFormOk;
    }

    function validateSubscribeAction() {
        if (form.find("input[name='wpySubscribeAction']")[0].value == "") {
            isFormOk = false;
        }
    }

    function validateReleaseType() {
        var isValid = validateMinOneOptionChecked("wpyReleaseTypes");
        toggleErrorMessage("errorMessageReleaseType", isValid);
    }

    function validateLanguage() {
        var isValid = validateMinOneOptionChecked("wpyLanguage");
        toggleErrorMessage("errorMessageLanguage", isValid);
    }

    function validateContactInfo() {
        var isValid = form.find("input[name='wpyEmail']")[0].value != "";
        isFormOk = !isValid ? false:isFormOk;
        toggleErrorMessage("errorMessageContactInfo", isValid);}

    function validateMinOneOptionChecked(inputName) {
        var isValid = form.find("input[name='" + inputName + "']:checked").length > 0;
        isFormOk = !isValid ? false:isFormOk;
        return isValid;
    }

    function toggleErrorMessage(warningClass, isValid) {
        isValid = !!isValid;
        var errorMessageContainer = form.find(".warning.feedback  .errorMessage." + warningClass);
        if (isValid) {
            errorMessageContainer.removeClass("displayErrorMessage");
        } else {
            errorMessageContainer.addClass("displayErrorMessage");
        }
    }

    function toggleErrorMessageContainer() {
        form.find(".warning.feedback")[0].style.display = form.find(".warning.feedback .errorMessage.displayErrorMessage").length > 0 ? "":"none";
    }

    function hideServerError() {
        toggleErrorMessage("serverError", true);
    }

    return {
        subscribe : function(evt, button) {
            setFormByChild(button);
            setSubscribeAction("begin");
        },

        unSubscribe : function(evt, button) {
            setFormByChild(button);
            setSubscribeAction("end");
        },

        validateForm : function(evt, theForm) {
            form = $(theForm);
            return doValidateForm();
        }
    }
})();


var ReportArchive = (function(){
    var numberOfVisibleMenuItems = 7;
    var menuItemWidth = 73;
    var menuItemCenterOffset = (numberOfVisibleMenuItems-1)/2 * menuItemWidth;
    var menuItemLastVisibleOffset = (numberOfVisibleMenuItems-1) * menuItemWidth + 1; //1 extra pixel for selected border
    var menu, reportContainer, menuItemGroup, maxMenuItemGroupPosition, activeMenuItem, activeReport;

    function doInit() {
        populateVariables();
        if (isInitOk()) {
            setUpOnClickHandlerForMenu();
            setUpMinHeightOnReportContainer();
            displayReportBasedOnLocationHash();
        }
    }
    
    function populateVariables() {
        menu = $(".reportArchive .navigation"); 
        reportContainer = $(".reportArchive .reportContainer");
        menuItemGroup = $(".reportArchive .navigation .menuItemGroup");
        if (menu.length == 1 && reportContainer.length == 1 && menuItemGroup.length == 1) {
            activeMenuItem = getActiveMenuItem();
            activeReport = getActiveReport();
            maxMenuItemGroupPosition = menuItemLastVisibleOffset - menuItemGroup.find(".menuItem:last").position().left;
        }
    }
    
    function isInitOk() {
        return activeMenuItem && activeReport && maxMenuItemGroupPosition;
    }
    
    function setUpOnClickHandlerForMenu() {
         menu.find(".menuItem").bind("click", onClickMenuItem);
    }
    
    function setUpMinHeightOnReportContainer() {
        var maxReportHeight = 0;
        reportContainer.find(".report").each(function(index, report) {
            var reportHeight = $(report).height();
            maxReportHeight = reportHeight > maxReportHeight ? reportHeight:maxReportHeight;
        });
        reportContainer.css("min-height", maxReportHeight+"px");
    }
    
    function displayReportBasedOnLocationHash() {
        if (document.location.hash.match(/#(\d{4})/)) {
            var year = RegExp.$1;
            showYear(menu.find(".reportYear" + year));
        }
    }

    function onClickMenuItem() {
        var el = $(this);
       
        if (el.hasClass("next")) {
            showYear(getNextYear());
        } else if (el.hasClass("previous")) {
            showYear(getPreviousYear());
        } else if (el.hasClass("menuItem")){
            showYear(el);
        }
    }
    
    function showYear(newActiveMenuItem) {
        if (newActiveMenuItem.length == 1 && newActiveMenuItem[0] != activeMenuItem[0]) {
            var newYear = getYearFromMenuItem(newActiveMenuItem);

            reportContainer.find(".report.selected").removeClass("selected");
            reportContainer.find(".report"+newYear).addClass("selected");
            
            newActiveMenuItem.addClass("selected");
            activeMenuItem.removeClass("selected");

            activeMenuItem = newActiveMenuItem;

            document.location.hash = "#"+newYear;
            
            updateMenuItemGroupPosition();
            
        } 
    }

    function getYearFromMenuItem(menuItem) {
        return menuItem[0].className.match(/reportYear(\d{4})/)[1];
    }
    
    function getNextYear() {
        //next year is previous node
        return activeMenuItem.prev();
    }

    function getPreviousYear() {
        //previous year is next node
        return activeMenuItem.next();
    }
    
    function getActiveMenuItem() {
        return menu.find(".menuItem.selected");
    }
    
    function getActiveReport() {
        return reportContainer.find(".report.selected");
    }

    function updateMenuItemGroupPosition() {
        var newContainerOffset = menuItemCenterOffset - activeMenuItem.position().left;
        if (newContainerOffset > 0) {
            newContainerOffset = 0;
        } else if(newContainerOffset < maxMenuItemGroupPosition) {
            newContainerOffset = maxMenuItemGroupPosition;
        }
        menuItemGroup.css("left", newContainerOffset);
    }
    
    return {
        init : function() {
            doInit();
        }
    }

})();
