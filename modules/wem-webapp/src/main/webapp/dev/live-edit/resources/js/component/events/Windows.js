(function () {
    var windows = AdminLiveEdit.components.events.Windows = function () {
        this.selector = '[data-live-edit-type=window]';
        this.renderEmptyPlaceholders();
        this.attachMouseOverEvent();
        this.attachMouseOutEvent();
        this.attachClickEvent();
    };
    // Inherit from Base prototype
    windows.prototype = new AdminLiveEdit.components.events.Base();

    // Fix constructor as it now is Base
    windows.constructor = windows;

    var p = windows.prototype;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    p.appendEmptyPlaceholder = function ($window) {
        var $placeholder = $liveedit('<div/>', {
            'class': 'live-edit-empty-window-placeholder',
            'html': 'Empty Window'
        });
        $window.append($placeholder);
    };


    p.isWindowEmpty = function ($window) {
        return $liveedit($window).children().length === 0;
    };


    p.renderEmptyPlaceholders = function () {
        var t = this;
        this.getAll().each(function (index) {
            var $window = $liveedit(this);
            var windowIsEmpty = t.isWindowEmpty($window);
            if (windowIsEmpty) {
                t.appendEmptyPlaceholder($window);
            }
        });
    };

}());