(function () {
    var contents = AdminLiveEdit.page.components.Contents = function () {
        this.selector = '[data-live-edit-type=content]';
        this.highlightColor = '#141414';
        this.attachMouseOverEvent();
        this.attachMouseOutEvent();
        this.attachClickEvent();
    };
    // Inherit from Base prototype
    contents.prototype = new AdminLiveEdit.page.components.Base();

    // Fix constructor as it now is Base
    contents.constructor = contents;

    var p = contents.prototype;

    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *


}());