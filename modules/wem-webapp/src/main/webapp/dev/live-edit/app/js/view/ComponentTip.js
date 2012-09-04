/**
 * TODO: As ComponentTip has changed look'n feel this object may be obsolete and we may use ToolTip instead.
 */
(function () {
    'use strict';

    // Class definition (constructor function)
    var componentTip = AdminLiveEdit.view.ComponentTip = function () {
        this.create();
        this.bindEvents();
    };

    // Inherits ui.Base
    componentTip.prototype = new AdminLiveEdit.view.Base();

    // Fix constructor as it now is Base
    componentTip.constructor = componentTip;

    // Shorthand ref to the prototype
    var p = componentTip.prototype;

    // Uses
    var util = AdminLiveEdit.Util;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    p.bindEvents = function () {
        $liveedit(window).on('component:select', $liveedit.proxy(this.show, this));

        $liveedit(window).on('component:deselect', $liveedit.proxy(this.hide, this));
    };


    p.create = function () {
        var self = this;

        var html = '<div class="live-edit-tool-tip" style="top:-5000px; left:-5000px;">' +
                   '    <span class="live-edit-tool-tip-type-text"></span>: ' +
                   '    <span class="live-edit-tool-tip-name-text"></span>' +
                   '</div>';

        self.createElement(html);
        self.appendTo($liveedit('body'));

        // Make sure component is not deselected when the conponentTip element is clicked.
        self.getEl().on('click', function (event) {
            event.stopPropagation();
        });
    };


    p.show = function (event, $component) {
        var self = this;

        var info = util.getComponentInfo($component);
        // Set text first so width is calculated correctly.
        self.setText(info.type, info.name);

        var componentBox = util.getBoxModel($component),
            leftPos = componentBox.left + (componentBox.width / 2 - self.getEl().width() / 2),
            topPos = componentBox.top - 32;

        if (info.type === 'page' && info.tagName === 'body') {
            topPos = 0;
        }

        self.setCssPosition($component);
        self.getEl().css({
            top: topPos,
            left: leftPos
        });
    };


    p.setText = function (componentType, componentName) {
        var $componentTip = this.getEl();
        $componentTip.children('.live-edit-tool-tip-type-text').text(componentType);
        $componentTip.children('.live-edit-tool-tip-name-text').text(componentName);
    };


    p.hide = function () {
        this.getEl().css({
            top: '-5000px',
            left: '-5000px'
        });
    };

}());