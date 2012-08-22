(function () {
    'use strict';

    var contents = AdminLiveEdit.components.Contents = function () {
        this.cssSelector = '[data-live-edit-type=content]';
        this.attachMouseOverEvent();
        this.attachMouseOutEvent();
        this.attachClickEvent();
    };
    // Inherit from Base prototype
    contents.prototype = new AdminLiveEdit.components.Base();

    // Fix constructor as it now is Base
    contents.constructor = contents;

    var p = contents.prototype;

    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *


}());