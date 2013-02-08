/**
 * TODO: As ComponentTip has changed look'n feel this object may be obsolete and we may use ToolTip instead.
 */
(function ($) {
    'use strict';

    // Namespaces
    AdminLiveEdit.view.componenttip = {};

    // Class definition (constructor function)
    var tip = AdminLiveEdit.view.componenttip.Tip = function () {
        this.addView();
        this.addEvents();
        this.menu = new AdminLiveEdit.view.componenttip.menu.Menu(this);
        this.registerGlobalListeners();
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

        var html = '<div class="live-edit-component-tip" style="top:-5000px; left:-5000px;">' +
                   '    <div class="live-edit-component-tip-left">' +
                   '        <img src="../app/images/drag-handle.png" class="live-edit-component-tip-icon-menu"/>' +
                   '    </div>' +
                   '    <div class="live-edit-component-tip-center">' +
                   '        <span class="live-edit-component-tip-name-text"></span>' +
                   '        <span class="live-edit-component-tip-type-text"></span> ' +
                   '    </div>' +
                   '    <div class="live-edit-component-tip-right">' +
                   '        <img src="../app/images/aiga-forward-and-right-arrow.png" class="live-edit-component-tip-icon-parent"/>' +
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
    };


    proto.show = function (event, $component) {
        var me = this;

        me.$selectedComponent = $component;

        var componentInfo = util.getComponentInfo($component);

        // Set text first so width is calculated correctly.
        me.setText(componentInfo.type, componentInfo.name);

        var componentBox = util.getBoxModel($component),
            leftPos = componentBox.left + (componentBox.width / 2 - me.getEl().outerWidth() / 2),
            topPos = componentBox.top - me.getEl().height() - 10;

        me.getEl().css({
            top: topPos,
            left: leftPos
        });

        if (!me.menu.hidden) {
            me.menu.show(event, $component);
        }
    };


    proto.getMenuButton = function () {
        return this.getEl().find('.live-edit-component-tip-icon-menu');
    };


    proto.getParentButton = function () {
        return this.getEl().find('.live-edit-component-tip-icon-parent');
    };


    proto.setText = function (componentType, componentName) {
        var $componentTip = this.getEl();
        $componentTip.find('.live-edit-component-tip-name-text').text(componentName);
        $componentTip.find('.live-edit-component-tip-type-text').text(componentType);
    };


    proto.hide = function () {
        this.$selectedComponent = null;

        this.getEl().css({
            top: '-5000px',
            left: '-5000px'
        });
    };

}($liveedit));