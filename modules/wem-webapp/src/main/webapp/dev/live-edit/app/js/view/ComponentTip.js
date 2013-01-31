/**
 * TODO: As ComponentTip has changed look'n feel this object may be obsolete and we may use ToolTip instead.
 */
(function ($) {
    'use strict';

    // Class definition (constructor function)
    var componentTip = AdminLiveEdit.view.ComponentTip = function () {
        this.addView();
        this.registerGlobalListeners();
    };

    // Inherits ui.Base
    componentTip.prototype = new AdminLiveEdit.view.Base();

    // Fix constructor as it now is Base
    componentTip.constructor = componentTip;

    // Shorthand ref to the prototype
    var proto = componentTip.prototype;

    // Uses
    var util = AdminLiveEdit.Util;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    proto.$selectedComponent = null;

    proto.registerGlobalListeners = function () {
        $(window).on('component:select', $.proxy(this.show, this));
        $(window).on('component:deselect', $.proxy(this.hide, this));
        $(window).on('component:remove', $.proxy(this.hide, this));
    };


    proto.addView = function () {
        var me = this;

        var html = '<div class="live-edit-component-tip" style="top:-5000px; left:-5000px;">' +
                   '    <span class="live-edit-component-tip-drag-handle"> </span> ' +
                   '    <span class="live-edit-component-tip-type-text"></span> ' +
                   '    <span class="live-edit-component-tip-name-text"></span>' +
                   '</div>';

        me.createElement(html);
        me.appendTo($('body'));

        // Make sure component is not deselected when the conponentTip element is clicked.
        me.getEl().on('click', function (event) {
            event.stopPropagation();
        });

        var $dragHandle = me.getDragHandle();

        $dragHandle.on('mousedown', function () {
            this.le_mouseIsDown = true;
            // TODO: Use PubSub
            AdminLiveEdit.DragDrop.enable();
        });

        $dragHandle.on('mousemove', function (event) {
            if (this.le_mouseIsDown) {

                this.le_mouseIsDown = false;
                // TODO: Get the selected using PubSub
                var $selectedComponent = me.$selectedComponent;

                var evt = document.createEvent('MouseEvents');
                evt.initMouseEvent('mousedown', true, true, window, 0, event.screenX, event.screenY, event.clientX, event.clientY, false,
                    false, false, false, 0, null);

                $selectedComponent[0].dispatchEvent(evt);

            }
        });
        $dragHandle.on('mouseup', function () {
            this.le_mouseIsDown = false;
            // TODO: remove reference to DragDrop, use PubSub.
            AdminLiveEdit.DragDrop.disable();
        });



    };


    proto.show = function (event, $component) {
        var me = this;

        me.$selectedComponent = $component;

        var componentInfo = util.getComponentInfo($component);

        me.showHideDragHandle(componentInfo);

        // Set text first so width is calculated correctly.
        me.setText(componentInfo.type, componentInfo.name);

        var componentBox = util.getBoxModel($component),
            leftPos = componentBox.left + (componentBox.width / 2 - me.getEl().outerWidth() / 2),
            topPos = componentBox.top - (me.getEl().height() * 2) - 2; // -2 to show above the highlighter border

        me.getEl().css({
            top: topPos,
            left: leftPos
        });
    };


    proto.showHideDragHandle = function (componentInfo) {
        var me = this;
        var $dragHandle = me.getDragHandle();
        if (componentInfo.type === 'window') {
            $dragHandle.css({'display': 'inline-block'});
        } else {
            $dragHandle.css({'display': 'none'});
        }
    };


    proto.getDragHandle = function () {
        return this.getEl().find('.live-edit-component-tip-drag-handle');
    };


    proto.setText = function (componentType, componentName) {
        var $componentTip = this.getEl();
        var typeText = componentType.concat(':');
        $componentTip.children('.live-edit-component-tip-type-text').text(typeText);
        $componentTip.children('.live-edit-component-tip-name-text').text(componentName);
    };


    proto.hide = function () {
        this.$selectedComponent = null;

        this.getEl().css({
            top: '-5000px',
            left: '-5000px'
        });
    };

}($liveedit));