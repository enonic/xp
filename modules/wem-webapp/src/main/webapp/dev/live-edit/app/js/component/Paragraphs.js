(function () {
    'use strict';

    var paragraphs = AdminLiveEdit.components.Paragraphs = function () {
        this.cssSelector = '[data-live-edit-type=paragraph]';
        this.attachMouseOverEvent();
        this.attachMouseOutEvent();
        this.attachClickEvent();
    };
    // Inherit from Base prototype
    paragraphs.prototype = new AdminLiveEdit.components.Base();

    // Fix constructor as it now is Base
    paragraphs.constructor = paragraphs;

    var p = paragraphs.prototype;

    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

}());