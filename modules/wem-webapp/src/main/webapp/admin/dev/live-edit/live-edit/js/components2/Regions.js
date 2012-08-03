(function () {
    var regions = AdminLiveEdit.components.Regions = function () {
        this.selector = '[data-live-edit-type=region]';
        this.attachMouseOverEvent();
        this.attachMouseOutEvent();
        this.attachClickEvent();
    };
    // Inherit from Base prototype
    regions.prototype = new AdminLiveEdit.components.Base();
    regions.constructor = regions;

    var p = regions.prototype;

    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    p.appendEmptyPlaceholder = function ($region) {
        var $placeholder = $liveedit('<div/>', {
            'class': 'live-edit-empty-region-placeholder',
            'html': 'Drag components here'
        });
        $region.append($placeholder);
    };


    p.isRegionEmpty = function ($region) {
        var hasNotWindows = $region.children(this.selector + ':not(:hidden)').length === 0;
        var hasNotDropTargetPlaceholder = $region.children('.live-edit-drop-target-placeholder').length === 0;
        return hasNotWindows && hasNotDropTargetPlaceholder;
    };


    p.removeAllRegionPlaceholders = function () {
        $liveedit('.live-edit-empty-region-placeholder').remove();
    };


    p.renderEmptyPlaceholders = function () {
        var t = this;
        t.removeAllRegionPlaceholders();
        var $regions = t.getAll();
        $regions.each(function (index) {
            var $region = $liveedit(this);
            var regionIsEmpty = t.isRegionEmpty($region);
            if (regionIsEmpty) {
                t.appendEmptyPlaceholder($region);
            }
        });
    };


    p.registerSubscribers = function () {
        var t = this;
        $liveedit.subscribe('/page/component/sortupdate', t.renderEmptyPlaceholders);
        $liveedit.subscribe('/page/component/sortupdate', t.renderEmptyPlaceholders);
        $liveedit.subscribe('/page/component/dragover', t.renderEmptyPlaceholders);
    };

}());