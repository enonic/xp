(function () {
    var paragraphs = AdminLiveEdit.page.components.Paragraphs = function () {
        this.selector = '[data-live-edit-type=paragraph]';
        this.highlightColor = '#141414';
        this.attachMouseOverEvent();
        this.attachMouseOutEvent();
        this.attachClickEvent();
    };
    // Inherit from Base prototype
    paragraphs.prototype = new AdminLiveEdit.page.components.Base();

    // Fix constructor as it now is Base
    paragraphs.constructor = paragraphs;

    var p = paragraphs.prototype;

    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

}());