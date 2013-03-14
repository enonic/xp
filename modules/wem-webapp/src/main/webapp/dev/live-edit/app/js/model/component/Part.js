(function ($) {
    'use strict';

    var parts =  AdminLiveEdit.model.component.Part = function () {
        this.cssSelector = '[data-live-edit-type=part]';
        this.renderEmptyPlaceholders();
        this.attachMouseOverEvent();
        this.attachMouseOutEvent();
        this.attachClickEvent();
    };
    // Inherit from Base prototype
    parts.prototype = new AdminLiveEdit.model.component.Base();

    // Fix constructor as it now is Base
    parts.constructor = parts;

    var proto = parts.prototype;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    proto.appendEmptyPlaceholder = function ($part) {
        var $placeholder = $('<div/>', {
            'class': 'live-edit-empty-part-placeholder',
            'html': 'Empty Part'
        });
        $part.append($placeholder);
    };


    proto.isPartEmpty = function ($part) {
        return $($part).children().length === 0;
    };


    proto.renderEmptyPlaceholders = function () {
        var t = this;
        this.getAll().each(function (index) {
            var $part = $(this);
            var partIsEmpty = t.isPartEmpty($part);
            if (partIsEmpty) {
                t.appendEmptyPlaceholder($part);
            }
        });
    };

}($liveedit));