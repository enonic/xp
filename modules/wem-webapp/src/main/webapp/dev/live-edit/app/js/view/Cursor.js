(function () {
    'use strict';

    // Class definition (constructor function)
    var cursor = AdminLiveEdit.view.Cursor = function () {
        this.bindEvents();
    };

    // Shorthand ref to the prototype
    var p = cursor.prototype;

    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    p.bindEvents = function () {
        $liveedit(window).on('component:mouseover', $liveedit.proxy(this.updateCursor, this));

        $liveedit(window).on('component:select', $liveedit.proxy(this.updateCursor, this));
    };


    p.updateCursor = function (event, $component) {
        var componentType = AdminLiveEdit.Util.getComponentType($component);
        var $body = $liveedit('body');
        var cursor = 'default';
        switch (componentType) {
        case 'region':
            cursor = 'pointer';
            break;
        case 'window':
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

}());