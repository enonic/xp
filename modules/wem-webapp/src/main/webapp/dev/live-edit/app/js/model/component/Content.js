(function () {
    'use strict';

    var contents = AdminLiveEdit.model.component.Content = function () {
        this.cssSelector = '[data-live-edit-type=content]';
        this.attachMouseOverEvent();
        this.attachMouseOutEvent();
        this.attachClickEvent();
    };
    // Inherit from Base prototype
    contents.prototype = new AdminLiveEdit.model.component.Base();

    // Fix constructor as it now is Base
    contents.constructor = contents;

    var proto = contents.prototype;

    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *


}());