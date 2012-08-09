(function () {
    // Class definition (constructor function)
    var toolTip = AdminLiveEdit.ui2.ToolTip = function () {
        this.OFFSET_X = 15;
        this.OFFSET_Y = 15;
        this.create();
        this.registerSubscribers();
    };

    // Inherits ui.Base
    toolTip.prototype = new AdminLiveEdit.ui2.Base();

    // Fix constructor as it now is Base
    toolTip.constructor = toolTip;

    // Shorthand ref to the prototype
    var p = toolTip.prototype;

    // Uses
    var util = AdminLiveEdit.Util;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *


    p.registerSubscribers = function () {
        var self = this;
        $liveedit.subscribe('/page/component/select', function () {
            self.hide.call(self);
        });
    };


    p.create = function () {
        var html ='<div class="live-edit-tool-tip" style="top:-5000px; left:-5000px;">' +
                  '    <img src="' + this.blankImage + '" style="padding-right: 7px; vertical-align: top"/>' +
                  '    <span class="live-edit-tool-tip-text"><!-- --></span>' +
                  '</div>';

        this.createElement(html);
        this.appendTo($liveedit('body'));
        this.attachEventListeners();
    };


    p.setIcon = function (componentType) {
        this.getEl().find('img').attr('src', util.getIconForComponent(componentType));
    };


    p.setText = function (text) {
        this.getEl().find('.live-edit-tool-tip-text').text(text);
    };


    p.attachEventListeners = function () {
        var self = this;

        $liveedit(document).on('mousemove', '[data-live-edit-type]', function (event) {
            // TODO: Make this more efficient.
            var isUi = $liveedit(event.target).is('.live-edit-info-tip, .live-edit-button') || $liveedit(event.target).parents('.live-edit-info-tip, .live-edit-button').length > 0;
            // TODO: Use PubSub instead of calling DragDrop object.
            if (isUi || AdminLiveEdit.ui2.DragDrop.isDragging()) {
                self.hide();
                return;
            }

            var $component = $liveedit(event.target).closest('[data-live-edit-type]');
            var type = util.getComponentType($component);
            var name = util.getComponentName($component);
            var pos = self.resolvePosition(event);

            self.getEl().css({
                top: pos.y,
                left: pos.x
            });
            self.setIcon(type);
            self.setText(name);
        });

        $liveedit(document).on('hover', '[data-live-edit-type]', function(event) {
            if (event.type === 'mouseenter') {
                self.getEl().hide().fadeIn(300);
            }
        });

        $liveedit(document).on('mouseout', function() {
            self.hide.call(self);
        });
    };


    p.resolvePosition = function (event) {
        var t = this;
        var pageX = event.pageX;
        var pageY = event.pageY;
        var x = pageX + t.OFFSET_X;
        var y = pageY + t.OFFSET_Y;
        var viewPortSize = util.getViewPortSize();
        var scrollTop = util.getDocumentScrollTop();
        var toolTipWidth = t.getEl().width();
        var toolTipHeight = t.getEl().height();

        if (x + toolTipWidth > (viewPortSize.width - t.OFFSET_X * 2)) {
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


    p.hide = function () {
        this.getEl().css({
            top: '-5000px',
            left: '-5000px'
        });
    };

}());