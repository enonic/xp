(function ($) {
    'use strict';

    var regions =  AdminLiveEdit.model.component.Regions = function () {
        this.cssSelector = '[data-live-edit-type=region]';
        this.renderEmptyPlaceholders();
        this.attachMouseOverEvent();
        this.attachMouseOutEvent();
        this.attachClickEvent();
        this.bindGlobalEvents();
    };

    // Inherit from Base prototype
    regions.prototype = new  AdminLiveEdit.model.component.Base();

    // Fix constructor as it now is Base
    regions.constructor = regions;

    // Shorthand ref to the prototype
    var p = regions.prototype;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *


    p.bindGlobalEvents = function () {
        $(window).on('component:drag:update', $.proxy(this.renderEmptyPlaceholders, this));

        $(window).on('component:drag:over', $.proxy(this.renderEmptyPlaceholders, this));
    };


    p.renderEmptyPlaceholders = function () {
        var me = this;
        me.removeAllRegionPlaceholders();
        var $regions = me.getAll();
        $regions.each(function (index) {
            var $region = $(this);
            var regionIsEmpty = me.isRegionEmpty.call(me, $region);
            if (regionIsEmpty) {
                me.appendEmptyPlaceholder.call(me, $region);
            }
        });
    };


    p.appendEmptyPlaceholder = function ($region) {
        var $placeholder = $('<div/>', {
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
        $('.live-edit-empty-region-placeholder').remove();
    };

}($liveedit));