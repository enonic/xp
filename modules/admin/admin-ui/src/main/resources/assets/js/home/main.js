require('webcomponents.js');
var $ = require('jquery');
var aboutDialogContainer, aboutDialog;

$(function () {

    var responsive = require('./responsive');
    responsive.applyResponsiveCls();
    window.onresize = responsive.applyResponsiveCls;

    var launcher = require('./launcher');
    launcher.init();

    setupAboutDialog();
});

function setupAboutDialog() {
    aboutDialogContainer = document.querySelector(".xp-about-dialog-container");
    aboutDialog = document.querySelector(".xp-about-dialog");

    document.querySelector(".xp-about").addEventListener("click", toggleAboutDialog);
    document.querySelector(".xp-about-dialog-button-close").addEventListener("click", hideAboutDialog);
    document.querySelector(".xp-about-dialog-corner-close").addEventListener("click", hideAboutDialog);
    aboutDialogContainer.addEventListener("click", handleClickOutside);
}

function toggleAboutDialog() {

    if (isAboutDialogShown()) {
        hideAboutDialog();
    }
    else {
        showAboutDialog();
    }
}

function isAboutDialogShown() {
    return window.getComputedStyle(aboutDialogContainer).getPropertyValue("display") !== "none";
}

function hideAboutDialog() {
    aboutDialogContainer.style.display = "none"
}

function showAboutDialog() {
    aboutDialogContainer.style.display = "flex"
}

function handleClickOutside(e) {
    if (!aboutDialog.contains(e.target)) {
        hideAboutDialog();
    }
}
