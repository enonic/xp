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
        $(window).on('component:mouseover', $.proxy(this.updateCursor, this));
        $(window).on('component:mouseout', $.proxy(this.resetCursor, this));
        $(window).on('component:select', $.proxy(this.updateCursor, this));
    };


    proto.updateCursor = function (event, $component) {
        var componentType = AdminLiveEdit.Util.getComponentType($component);
        var $body = $('body');
        var cursor = 'default';
        switch (componentType) {
        case 'region':
            cursor = 'pointer';
            break;
        case 'part':
            cursor = 'move';
            break;
        case 'paragraph':
            cursor = 'url(../app/images/pencil_16x16.png), default';
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