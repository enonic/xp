(function ($) {
    'use strict';

    // Class definition (constructor function)
    var toolTip = AdminLiveEdit.view.ToolTip = function () {
        this.OFFSET_X = 0;
        this.OFFSET_Y = 18;
        this.addView();
        this.attachEventListeners();
        this.registerGlobalListeners();
    };

    // Inherits ui.Base
    toolTip.prototype = new AdminLiveEdit.view.Base();

    // Fix constructor as it now is Base
    toolTip.constructor = toolTip;

    // Shorthand ref to the prototype
    var proto = toolTip.prototype;

    // Uses
    var util = AdminLiveEdit.Util;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    proto.registerGlobalListeners = function () {
        $(window).on('component:click:select', $.proxy(this.hide, this));
    };


    proto.addView = function () {
        var me = this;
        var html = '<div class="live-edit-tool-tip" style="top:-5000px; left:-5000px;">' +
                   '    <span class="live-edit-tool-tip-name-text"></span>' +
                   '    <span class="live-edit-tool-tip-type-text"></span> ' +
                   '</div>';

        me.createElement(html);
        me.appendTo($('body'));
    };


    proto.setText = function (componentType, componentName) {
        var $tooltip = this.getEl();
        $tooltip.children('.live-edit-tool-tip-type-text').text(componentType);
        $tooltip.children('.live-edit-tool-tip-name-text').text(componentName);
    };


    proto.attachEventListeners = function () {
        var me = this;

        $(document).on('mousemove', '[data-live-edit-type]', function (event) {
            var targetIsUiComponent = $(event.target).is('[id*=live-edit-ui-cmp]') ||
                                      $(event.target).parents('[id*=live-edit-ui-cmp]').length > 0;

            // TODO: Use PubSub instead of calling DragDrop object.
            var pageHasComponentSelected = $('.live-edit-selected-component').length > 0;
            if (targetIsUiComponent || pageHasComponentSelected || AdminLiveEdit.DragDropSort.isDragging()) {
                me.hide();
                return;
            }

            var $component = $(event.target).closest('[data-live-edit-type]');
            var componentInfo = util.getComponentInfo($component);
            var pos = me.getPosition(event);

            me.getEl().css({
                top: pos.y,
                left: pos.x
            });

            me.setText(componentInfo.type, componentInfo.name);
        });

        $(document).on('hover', '[data-live-edit-type]', function (event) {
            if (event.type === 'mouseenter') {
                me.getEl().hide().fadeIn(300);
            }
        });

        $(document).on('mouseout', function () {
            me.hide.call(me);
        });
    };


    proto.getPosition = function (event) {
        var t = this;
        var pageX = event.pageX;
        var pageY = event.pageY;
        var x = pageX + t.OFFSET_X;
        var y = pageY + t.OFFSET_Y;
        var viewPortSize = util.getViewPortSize();
        var scrollTop = util.getDocumentScrollTop();
        var toolTipWidth = t.getEl().width();
        var toolTipHeight = t.getEl().height();

        if (x + toolTipWidth > (viewPortSize.width - t.OFFSET_X * 2) - 50) {
            x = pageX - toolTipWidth - (t.OFFSET_X * 2);
        }
        if (y + toolTipHeight > (viewPortSize.height + scrollTop - t.OFFSET_Y * 2)) {
            y = pageY - toolTipHeight - (t.OFFSET_Y * 2);
        }

        return {
            x: x,
            y: y
        };
    };


    proto.hide = function () {
        this.getEl().css({
            top: '-5000px',
            left: '-5000px'
        });
    };

}($liveedit));