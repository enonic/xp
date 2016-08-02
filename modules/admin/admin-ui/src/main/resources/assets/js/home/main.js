var $ = require('jquery');

$(function () {
    var launcher = require('./launcher');
    launcher.init();

    setupAboutDialog();

    var sessionExpiredDetector = require('./sessionExpiredDetector');
    sessionExpiredDetector.startPolling();

    var xptour = require('./xptour');
    xptour.init();
});

function setupAboutDialog() {
    var aboutDialog = new api.ui.dialog.ModalDialog({title: new api.ui.dialog.ModalDialogHeader("")});
    aboutDialog.addClass("xp-about-dialog");
    aboutDialog.appendChildToContentPanel(getAboutDialogContent());
    document.querySelector(".xp-about").addEventListener("click", function () {
        aboutDialog.open();
        aboutDialog.centerHorisontally();
    });
    api.dom.Body.get().appendChild(aboutDialog);
}

function getAboutDialogContent() {
    var html = '<div class="xp-about-dialog-content">' +
               '    <div class="xp-about-dialog-app-icon">' +
               '        <img src="/admin/common/images/app-icon.svg">' +
               '    </div>' +
               '    <h1>Enonic XP</h1>' +
               '    <div class="xp-about-dialog-version-block">' +
               '        <span class="xp-about-dialog-version">' + CONFIG.xpVersion + '</span>&nbsp;' +
               '        <a href="' + CONFIG.docLinkPrefix + '/appendix/release-notes/" target="_blank">What\'s new</a>' +
               '    </div>' +
               '    <div class="xp-about-dialog-text">' +
               'Blend sites, applications and services together seamlessly. ' +
               'Our powerful Web Operating System simplifies all stages of the ' +
               'digital delivery process - focus on solution rather than technology.' +
               '    </div>' +
               '</div>';

    var element = api.dom.Element.fromString(html);
    return element;
}