require('webcomponents.js');
var $ = require('jquery');

$(function () {

    var responsive = require('./responsive');
    responsive.applyResponsiveCls();
    window.onresize = responsive.applyResponsiveCls;

    var launcher = require('./launcher');
    launcher.init();

});
