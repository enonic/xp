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
        me.menu = new AdminLiveEdit.view.menu.Menu();
        me.menu.trigger = me;
        me.$selectedComponent = null;

        me.addView();
        me.addEvents();
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



    proto.registerGlobalListeners = function () {
        $(window).on('component:click:select', $.proxy(this.show, this));
        $(window).on('component:click:deselect', $.proxy(this.hide, this));
        $(window).on('component:remove', $.proxy(this.hide, this));
        $(window).on('component:sort:start', $.proxy(this.hide, this));
        $(window).on('component:paragraph:edit:init', $.proxy(this.hide, this));
    };


    proto.addView = function () {
        var me = this;

        var html = '<div class="live-edit-component-tip live-edit-component-tip-arrow-bottom" style="top:-5000px; left:-5000px;">' +
                   '    <div class="live-edit-component-tip-left">' +
                   '        <div title="Show Menu" class="live-edit-component-tip-icon-menu"></div>' +
                   '    </div>' +
                   '    <div class="live-edit-component-tip-center">' +
                   '        <span class="live-edit-component-tip-name-text"></span>' +
                   '        <span class="live-edit-component-tip-type-text"></span> ' +
                   '    </div>' +
                   '    <div class="live-edit-component-tip-right">' +
                   '        <div title="Select parent component" class="live-edit-component-tip-icon-parent"></div>' +
                   '        <div title="Deselect" class="live-edit-component-tip-icon-deselect" style="display: none"></div>' +
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
            me.handleMenuButtonClick();
        });

        me.getParentButton().click(function () {
            var $parent = me.$selectedComponent.parents('[data-live-edit-type]');
            if ($parent && $parent.length > 0) {
                $(window).trigger('component:click:select', [$($parent[0])]);
            }
        });

        me.getDeselectButton().click(function () {
            $(window).trigger('component:click:deselect');
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
        me.toggleArrowPosition(true);
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
        me.toggleArrowPosition(false);
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


    proto.hide = function () {
        var me = this;
        this.$selectedComponent = null;

        this.getEl().css({
            top: '-5000px',
            left: '-5000px'
        });

        me.getMenuButton().removeClass('live-edit-component-tip-icon-menu-selected');
    };


    proto.setText = function ($component) {
        var $componentTip = this.getEl(),
            componentInfo = util.getComponentInfo($component);
        $componentTip.find('.live-edit-component-tip-name-text').text(componentInfo.name);
        $componentTip.find('.live-edit-component-tip-type-text').text(componentInfo.type === 'page' ? componentInfo.key : componentInfo.type);
    };


    proto.toggleArrowPosition = function (isPageComponent) {
        var me = this;
        if (isPageComponent) {
            me.getEl().removeClass('live-edit-component-tip-arrow-bottom').addClass('live-edit-component-tip-arrow-top');
        } else {
            me.getEl().removeClass('live-edit-component-tip-arrow-top').addClass('live-edit-component-tip-arrow-bottom');
        }
    };


    proto.toggleRightSideButton = function (isPageComponent) {
        var me = this;
        me.getParentButton().css('display', isPageComponent ? 'none' : 'block');
        me.getDeselectButton().css('display', isPageComponent ? 'block' : 'none');
    };


    proto.handleMenuButtonClick = function () {
        var me = this;
        var $menuButton = me.getMenuButton();
        if (!$menuButton.hasClass('live-edit-component-tip-icon-menu-selected')) {
            $menuButton.addClass('live-edit-component-tip-icon-menu-selected');
            $menuButton.attr('title', 'Hide menu');
            $menuButton.attr('alt', 'Hide menu');
        } else {
            $menuButton.removeClass('live-edit-component-tip-icon-menu-selected');
            $menuButton.attr('title', 'Show menu');
            $menuButton.attr('alt', 'Show menu');
        }

        if (me.$selectedComponent) {
            $(window).trigger('tip:menu:click', [me.$selectedComponent]);
        }
    };


    proto.getMenuButton = function () {
        return this.getEl().find('.live-edit-component-tip-icon-menu');
    };


    proto.getParentButton = function () {
        return this.getEl().find('.live-edit-component-tip-icon-parent');
    };


    proto.getDeselectButton = function () {
        return this.getEl().find('.live-edit-component-tip-icon-deselect');
    };

}($liveedit));