(function () {
    var page = AdminLiveEdit.page.components.Page = function () {
        this.selector = '[data-live-edit-type=page]';
        this.highlightColor = '#141414';
        this.attachMouseOverEvent();
        this.attachMouseOutEvent();
        this.attachClickEvent();
    };
    // Inherit from Base prototype
    page.prototype = new AdminLiveEdit.page.components.Base();

    // Fix constructor as it now is Base
    page.constructor = page;

    var p = page.prototype;

    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

}());