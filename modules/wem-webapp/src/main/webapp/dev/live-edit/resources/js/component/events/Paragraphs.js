(function () {
    var paragraphs = AdminLiveEdit.components.events.Paragraphs = function () {
        this.selector = '[data-live-edit-type=paragraph]';
        this.attachMouseOverEvent();
        this.attachMouseOutEvent();
        this.attachClickEvent();
    };
    // Inherit from Base prototype
    paragraphs.prototype = new AdminLiveEdit.components.events.Base();

    // Fix constructor as it now is Base
    paragraphs.constructor = paragraphs;

    var p = paragraphs.prototype;

    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

}());