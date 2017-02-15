var $ = require('jquery');

$(function () {
    var launcher = require('./launcher');
    launcher.init();

    setupAboutDialog();

    var sessionExpiredDetector = require('./sessionExpiredDetector');
    sessionExpiredDetector.startPolling();

    var xptour = require('./xptour');
    var tourDialog = xptour.init();

    var enonicXPTourCookie = api.util.CookieHelper.getCookie("enonic_xp_tour");
    if (!enonicXPTourCookie) {
        api.util.CookieHelper.setCookie("enonic_xp_tour", "tour", 365);
        setTimeout(function () {
            tourDialog.open();
        }, 100);
    }

    document.querySelector(".xp-tour").addEventListener("click", function () {
        tourDialog.open();
        setupBodyClickListeners(tourDialog);
    });

});

function setupBodyClickListeners(dialog) {
    var bodyEl = api.ui.mask.BodyMask.get().getHTMLElement(),
        listener = function (e) {
            e.stopPropagation();
            e.preventDefault();
            if (dialog.isVisible()) {
                dialog.close();
            }
            bodyEl.removeEventListener("click", listener);
        };
    bodyEl.addEventListener("click", listener);
}

function setupAboutDialog() {
    var aboutDialog = new api.ui.dialog.ModalDialog("", true);
    aboutDialog.addClass("xp-about-dialog");
    aboutDialog.appendChildToContentPanel(getAboutDialogContent());
    document.querySelector(".xp-about").addEventListener("click", function () {
        aboutDialog.open();
        setupBodyClickListeners(aboutDialog);
    });
    api.dom.Body.get().appendChild(aboutDialog);
}

function getAboutDialogContent() {
    var html = '<div class="xp-about-dialog-content">' +
               '    <div class="xp-about-dialog-app-icon">' +
               '        <img src="' + CONFIG.adminUrl + '/common/images/app-icon.svg">' +
               '    </div>' +
               '    <h1>Enonic XP</h1>' +
               '    <div class="xp-about-dialog-version-block">' +
               '        <span class="xp-about-dialog-version">' + CONFIG.xpVersion + '</span>&nbsp;&nbsp;' +
               '        <a href="' + CONFIG.docLinkPrefix + '/appendix/release-notes/" target="_blank">What\'s new</a>' +
               '    </div>' +
               '    <div class="xp-about-dialog-text">' +
               'The Web Operating System - ‌‌Designed to simplify development and delivery of digital experiences and services.<br><br>' +
               'Built with <span style="color: red;">♥</span> by the Enonic Team.' +
               '    </div>' +
               '    <div class="xp-about-dialog-footer">' +
               '        <a href="https://github.com/enonic/xp/blob/master/LICENSE.txt" target="_blank">Licensing</a>' +
               '        <a href="https://github.com/enonic/xp/" target="_blank">Source Code</a>' +
               '        <a href="https://enonic.com/downloads" target="_blank">Downloads</a>' +
               '    </div>' +
               '</div>';

    var element = api.dom.Element.fromString(html);
    return element;
}