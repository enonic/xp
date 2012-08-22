(function () {
    'use strict';

    var page = AdminLiveEdit.components.Page = function () {
        this.cssSelector = '[data-live-edit-type=page]';
        this.attachMouseOverEvent();
        this.attachMouseOutEvent();
        this.attachClickEvent();
    };
    // Inherit from Base prototype
    page.prototype = new AdminLiveEdit.components.Base();

    // Fix constructor as it now is Base
    page.constructor = page;

    var p = page.prototype;

    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

}());