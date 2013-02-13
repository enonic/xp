(function () {
    'use strict';

    var page = AdminLiveEdit.model.component.Page = function () {
        this.cssSelector = '[data-live-edit-type=page]';
        // this.attachMouseOverEvent();
        // this.attachMouseOutEvent();
        this.attachClickEvent();
    };
    // Inherit from Base prototype
    page.prototype = new AdminLiveEdit.model.component.Base();

    // Fix constructor as it now is Base
    page.constructor = page;

    var proto = page.prototype;

    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *


}());