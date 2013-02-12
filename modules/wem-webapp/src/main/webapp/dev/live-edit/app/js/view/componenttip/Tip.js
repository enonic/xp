/**
 * TODO: As ComponentTip has changed look'n feel this object may be obsolete and we may use ToolTip instead.
 */
(function ($) {
    'use strict';

    // Namespaces
    AdminLiveEdit.view.componenttip = {};

    // Class definition (constructor function)
    var tip = AdminLiveEdit.view.componenttip.Tip = function () {
        var me = this;
        me.addView();
        me.addEvents();
        me.menu = new AdminLiveEdit.view.componenttip.menu.Menu();
        me.menu.trigger = me;
        me.registerGlobalListeners();
    };

    // Inherits ui.Base
    tip.prototype = new AdminLiveEdit.view.Base();

    // Fix constructor as it now is Base
    tip.constructor = tip;

    // Shorthand ref to the prototype
    var proto = tip.prototype;

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

        var html = '<div class="live-edit-component-tip live-edit-component-tip-arrow-bottom" style="top:-5000px; left:-5000px;">' +
                   '    <div class="live-edit-component-tip-left">' +
                   '        <img src="../app/images/drag-handle.png" class="live-edit-component-tip-icon-menu"/>' +
                   '    </div>' +
                   '    <div class="live-edit-component-tip-center">' +
                   '        <span class="live-edit-component-tip-name-text"></span>' +
                   '        <span class="live-edit-component-tip-type-text"></span> ' +
                   '    </div>' +
                   '    <div class="live-edit-component-tip-right">' +
                   '        <img src="../app/images/aiga-forward-and-right-arrow.png" class="live-edit-component-tip-icon-parent"/>' +
                   '        <img src="../app/images/navigate_cross.png" class="live-edit-component-tip-icon-x" style="display: none"/>' +
                   '    </div>' +
                   '</div>';

        me.createElement(html);
        me.appendTo($('body'));
    };


    proto.addEvents = function () {
        var me = this;

        // Make sure component is not deselected when clicked.
        me.getEl().on('click', function (event) {
            event.stopPropagation();
        });

        me.getMenuButton().click(function () {
            if (me.$selectedComponent) {
                $(window).trigger('tip:menu:toggle', [me.$selectedComponent]);
            }
        });

        me.getParentButton().click(function () {
            var $parent = me.$selectedComponent.parents('[data-live-edit-type]');
            if ($parent && $parent.length > 0) {
                $(window).trigger('component:select', [$($parent[0])]);
            }
        });

        me.getXButton().click(function () {
            // Empty
        });
    };


    proto.show = function (event, $component) {
        var me = this;
        me.$selectedComponent = $component;

        // Set text first so width is calculated correctly.
        // For page we'll use the key.
        me.setText($component);

        if (util.getComponentType($component) === 'page') {
            me.showForPage($component);
        } else {
            me.showForComponent($component);
        }

        if (!me.menu.hidden) {
            me.menu.show(event, $component);
        }
    };


    proto.showForPage = function ($component) {
        var me = this;
        me.toggleTipArrowPosition(true);
        me.toggleRightSideButton(true);
        var componentBox = util.getBoxModel($component),
            leftPos = componentBox.left + (componentBox.width / 2 - me.getEl().outerWidth() / 2);

        me.getEl().css({
            position: 'fixed',
            top: '10px',
            left: leftPos
        });
    };


    proto.showForComponent = function ($component) {
        var me = this;
        me.toggleTipArrowPosition(false);
        me.toggleRightSideButton(false);
        var componentBox = util.getBoxModel($component),
            leftPos = componentBox.left + (componentBox.width / 2 - me.getEl().outerWidth() / 2),
            topPos = componentBox.top - me.getEl().height() - 10;

        me.getEl().css({
            position: 'absolute',
            top: topPos,
            left: leftPos
        });
    };


    proto.setText = function ($component) {
        var $componentTip = this.getEl(),
            componentInfo = util.getComponentInfo($component);
        $componentTip.find('.live-edit-component-tip-name-text').text(componentInfo.name);
        $componentTip.find('.live-edit-component-tip-type-text').text(componentInfo.type === 'page' ? componentInfo.key : componentInfo.type);
    };


    proto.hide = function () {
        this.$selectedComponent = null;

        this.getEl().css({
            top: '-5000px',
            left: '-5000px'
        });
    };


    proto.toggleTipArrowPosition = function (isPageComponent) {
        var me = this;
        if (isPageComponent) {
            me.getEl().removeClass('live-edit-component-tip-arrow-bottom').addClass('live-edit-component-tip-arrow-top');
        } else {
            me.getEl().removeClass('live-edit-component-tip-arrow-top').addClass('live-edit-component-tip-arrow-bottom');
        }
    };


    proto.toggleRightSideButton = function (isPageComponent) {
        var me = this;
        me.getParentButton().css('display', isPageComponent ? 'none' : 'inline');
        me.getXButton().css('display', isPageComponent ? 'inline' : 'none');
    };


    proto.getMenuButton = function () {
        return this.getEl().find('.live-edit-component-tip-icon-menu');
    };


    proto.getParentButton = function () {
        return this.getEl().find('.live-edit-component-tip-icon-parent');
    };


    proto.getXButton = function () {
        return this.getEl().find('.live-edit-component-tip-icon-x');
    };

}($liveedit));