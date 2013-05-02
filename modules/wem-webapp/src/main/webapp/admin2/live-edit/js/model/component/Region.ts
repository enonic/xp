AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.model.component');

(function ($) {
    'use strict';

    var regions = AdminLiveEdit.model.component.Region = function () {
        this.cssSelector = '[data-live-edit-type=region]';
        this.renderEmptyPlaceholders();

        this.attachMouseOverEvent();
        this.attachMouseOutEvent();
        this.attachClickEvent();

        this.registerGlobalListeners();
    };

    // Inherit from Base prototype
    regions.prototype = new AdminLiveEdit.model.component.Base();

    // Fix constructor as it now is Base
    // regions.constructor = regions;

    // Shorthand ref to the prototype
    var proto = regions.prototype;

    // uses
    var util = AdminLiveEdit.Util;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *


    proto.registerGlobalListeners = function () {
        $(window).on('component.onSortUpdate', $.proxy(this.renderEmptyPlaceholders, this));
        $(window).on('component.onSortOver', $.proxy(this.renderEmptyPlaceholders, this));
        $(window).on('component.onRemove', $.proxy(this.renderEmptyPlaceholders, this));
    };


    proto.renderEmptyPlaceholders = function () {
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


    proto.appendEmptyPlaceholder = function ($region) {
        var html = '<div>Drag components here</div>';
        html += '<div style="font-size: 10px;">' + util.getComponentName($region) + '</div>';
        var $placeholder = $('<div/>', {
            'class': 'live-edit-empty-region-placeholder',
            'html': html
        });
        $region.append($placeholder);
    };


    proto.isRegionEmpty = function ($region) {
        var hasNotParts = $region.children('[data-live-edit-type]' + ':not(:hidden)').length === 0;
        var hasNotDropTargetPlaceholder = $region.children('.live-edit-drop-target-placeholder').length === 0;
        return hasNotParts && hasNotDropTargetPlaceholder;
    };


    proto.removeAllRegionPlaceholders = function () {
        $('.live-edit-empty-region-placeholder').remove();
    };

}($liveedit));