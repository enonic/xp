(function () {
    var regions = AdminLiveEdit.page.components.Regions = function () {
        this.selector = '[data-live-edit-type=region]';
        this.highlightColor = '#141414';
        this.renderEmptyPlaceholders();
        this.attachMouseOverEvent();
        this.attachMouseOutEvent();
        this.attachClickEvent();
        this.registerSubscribers();
    };

    // Inherit from Base prototype
    regions.prototype = new AdminLiveEdit.page.components.Base();

    // Fix constructor as it now is Base
    regions.constructor = regions;

    // Shorthand ref to the prototype
    var p = regions.prototype;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *


    p.registerSubscribers = function () {
        var self = this;
        $liveedit.subscribe('/page/component/sortupdate', function () {
            self.renderEmptyPlaceholders.call(self);
        });

        $liveedit.subscribe('/page/component/dragover', function () {
            self.renderEmptyPlaceholders.call(self);
        });
    };


    p.renderEmptyPlaceholders = function () {
        var self = this;
        self.removeAllRegionPlaceholders();
        var $regions = self.getAll();
        $regions.each(function (index) {
            var $region = $liveedit(this);
            var regionIsEmpty = self.isRegionEmpty.call(self, $region);
            if (regionIsEmpty) {
                self.appendEmptyPlaceholder.call(self, $region);
            }
        });
    };


    p.appendEmptyPlaceholder = function ($region) {
        var $placeholder = $liveedit('<div/>', {
            'class': 'live-edit-empty-region-placeholder',
            'html': 'Drag components here'
        });
        $region.append($placeholder);
    };


    p.isRegionEmpty = function ($region) {
        var hasNotWindows = $region.children('[data-live-edit-type]' + ':not(:hidden)').length === 0;
        var hasNotDropTargetPlaceholder = $region.children('.live-edit-drop-target-placeholder').length === 0;
        return hasNotWindows && hasNotDropTargetPlaceholder;
    };


    p.removeAllRegionPlaceholders = function () {
        $liveedit('.live-edit-empty-region-placeholder').remove();
    };

}());