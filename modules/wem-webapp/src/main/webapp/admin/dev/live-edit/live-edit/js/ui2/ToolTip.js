(function () {
    // Class definition (constructor function)
    var toolTip = AdminLiveEdit.ui2.ToolTip = function () {
        this.OFFSET_X = 15;
        this.OFFSET_Y = 15;

        this.create();
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


    p.create = function () {
        var html ='<div class="live-edit-tool-tip" style="top:-5000px; left:-5000px;">' +
                  '    <img src="../live-edit/images/component_blue.png" style="padding-right: 7px; vertical-align: top"/>' +
                  '    <span class="live-edit-tool-tip-text"><!-- --></span>' +
                  '</div>';

        this.createElement(html);
        this.appendTo($liveedit('body'));
        this.attachEventListeners();
        this.registerSubscribers();
    };


    p.registerSubscribers = function () {
        $liveedit.subscribe('/page/component/select', this.hide);
    };


    p.updateIcon = function (componentType) {
        this.getEl().find('img').attr('src', util.getIconForComponent(componentType));
    };


    p.updateText = function (text) {
        this.getEl().find('.live-edit-tool-tip-text').text(text);
    };


    p.attachEventListeners = function () {
        var t = this;

        $liveedit(document).on('mousemove', '[data-live-edit-type]', function (event) {
            var $component = $liveedit(event.target).closest('[data-live-edit-type]');
            var type = util.getTypeFromComponent($component);
            var name = util.getNameFromComponent($component);
            var pos = t.resolvePosition(event);

            t.getEl().css({
                top: pos.y,
                left: pos.x
            });
            t.updateIcon(type);
            t.updateText(name);
        });

        $liveedit(document).on('mouseout', t.hide.call(t));
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