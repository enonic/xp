AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.view');

(function ($) {
    'use strict';

    // Class definition (constructor function)
    var cursor = AdminLiveEdit.view.Cursor = function () {
        this.registerGlobalListeners();
    };

    // Shorthand ref to the prototype
    var proto = cursor.prototype;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    proto.registerGlobalListeners = function () {
        $(window).on('component.mouseOver', $.proxy(this.updateCursor, this));
        $(window).on('component.mouseOut', $.proxy(this.resetCursor, this));
        $(window).on('component.onSelect', $.proxy(this.updateCursor, this));
    };


    proto.updateCursor = function (event, $component) {
        var componentType = liveedit.ComponentHelper.getComponentType($component);
        var $body = $('body');
        var cursor = 'default';

        switch (componentType) {
        case 'region':
            cursor = 'pointer';
            break;
        case 'part':
            cursor = 'move';
            break;
        case 'layout':
            cursor = 'move';
            break;
        case 'paragraph':
            cursor = 'move';
            break;
        default:
            cursor = 'default';
        }

        $body.css('cursor', cursor);
    };


    proto.resetCursor = function () {
        $('body').css('cursor', 'default');
    };

}($liveedit));