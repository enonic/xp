(function () {
    'use strict';

    var paragraphs =  AdminLiveEdit.model.component.Paragraphs = function () {
        this.cssSelector = '[data-live-edit-type=paragraph]';
        this.attachMouseOverEvent();
        this.attachMouseOutEvent();
        this.attachClickEvent();
    };
    // Inherit from Base prototype
    paragraphs.prototype = new  AdminLiveEdit.model.component.Base();

    // Fix constructor as it now is Base
    paragraphs.constructor = paragraphs;

    var proto = paragraphs.prototype;

    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

}());