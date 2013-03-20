AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.view.menu');

(function ($) {
    'use strict';

    // Class definition (constructor)
    var menu = AdminLiveEdit.view.menu.Menu = function () {
        var me = this;
        me.$selectedComponent = null;
        me.hidden = true;
        me.buttons = [];

        me.buttonConfig = {
            'page': ['settings', 'reset'],
            'region': ['parent', 'settings', 'reset', 'clear'],
            'part': ['parent', 'settings', 'details', 'remove'],
            'content': ['parent', 'view', 'edit'],
            'paragraph': ['parent', 'edit']
        };

        me.addView();

        me.registerEvents();

        me.registerGlobalListeners();
    };


    // Inherits Base.js
    menu.prototype = new AdminLiveEdit.view.Base();

    // Fix constructor as it now is Base
    menu.constructor = menu;

    // Shorthand ref to the prototype
    var proto = menu.prototype;

    // Uses
    var util = AdminLiveEdit.Util;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    var html = '';
    html += '<div class="live-edit-component-menu live-edit-arrow-top" style="display: none">';
    html += '   <div class="live-edit-component-menu-title-bar">';
    html += '       <div class="live-edit-component-menu-title-icon"><div><!-- --></div></div>';
    html += '       <div class="live-edit-component-menu-title-text"><!-- populated --></div>';
    html += '       <div class="live-edit-component-menu-title-close-button"><!-- --></div>';
    html += '   </div>';
    html += '   <div class="live-edit-component-menu-items">';
    html += '   </div>';
    html += '</div>';


    proto.registerGlobalListeners = function () {
        $(window).on('component.onSelect', $.proxy(this.show, this));
        $(window).on('component.onDeSelect', $.proxy(this.hide, this));
        $(window).on('component:sort:start', $.proxy(this.fadeOutAndHide, this));
        $(window).on('component.remove', $.proxy(this.hide, this));
        $(window).on('component.onParagraphEdit', $.proxy(this.hide, this));
    };


    proto.addView = function () {
        var me = this;

        me.createElement(html);
        me.appendTo($('body'));
        me.addButtons();
    };


    proto.registerEvents = function () {
        var me = this;
        $(me.getEl()).draggable({ handle: '.live-edit-component-menu-title-bar' });

        me.getCloseButton().click(function () {
            $(window).trigger('component.onDeSelect');
        });
    };


    proto.show = function (event, $component, pagePosition) {
        var me = this,
            componentInfo = util.getComponentInfo($component);

        me.$selectedComponent = $component;

        me.updateTitleBar($component);
        me.updateMenuItemsForComponent($component);
        me.moveToXY(pagePosition.x, pagePosition.y);
        me.getEl().show();

        this.hidden = false;
    };


    proto.hide = function () {
        this.getEl().hide();
        this.hidden = true;
    };


    proto.fadeOutAndHide = function () {
        var me = this;
        me.getEl().fadeOut(500, function () {
            me.hide();
            $(window).trigger('component.onDeSelect', {showComponentBar: false});
        });
        me.$selectedComponent = null;
    };


    proto.moveToXY = function (x, y) {
        this.getEl().css({
            left: x,
            top: y
        });
    };


    proto.addButtons = function () {
        var me = this;
        var parentButton = new AdminLiveEdit.view.menu.ParentButton(me);
        var settingsButton = new AdminLiveEdit.view.menu.SettingsButton(me);
        var detailsButton = new AdminLiveEdit.view.menu.DetailsButton(me);
        var insertButton = new AdminLiveEdit.view.menu.InsertButton(me);
        var resetButton = new AdminLiveEdit.view.menu.ResetButton(me);
        var clearButton = new AdminLiveEdit.view.menu.ClearButton(me);
        var viewButton = new AdminLiveEdit.view.menu.ViewButton(me);
        var editButton = new AdminLiveEdit.view.menu.EditButton(me);
        var removeButton = new AdminLiveEdit.view.menu.RemoveButton(me);

        var i,
            $menuItemsPlaceholder = me.getMenuItemsPlaceholderElement();
        for (i = 0; i < me.buttons.length; i++) {
            me.buttons[i].appendTo($menuItemsPlaceholder);
        }
    };


    proto.updateMenuItemsForComponent = function ($component) {
        var componentType = util.getComponentType($component);
        if (this.buttonConfig.hasOwnProperty(componentType)) {
            var buttonArray = this.buttonConfig[componentType];
            var buttons = this.getButtons();

            var i;
            for (i = 0; i < buttons.length; i++) {
                var $button = buttons[i].getEl();
                var id = $button.attr('data-live-edit-ui-cmp-id');
                var subStr = id.substring(id.lastIndexOf('-') + 1, id.length);
                if (buttonArray.indexOf(subStr) > -1) {
                    $button.show();
                } else {
                    $button.hide();
                }
            }
        }
    };


    proto.updateTitleBar = function ($component) {
        var componentInfo = util.getComponentInfo($component);
        this.setTitle(componentInfo.name);
        this.setIcon(componentInfo.type);
    };


    proto.setTitle = function (titleText) {
        this.getTitleElement().text(titleText);
    };


    proto.setIcon = function (componentType) {
        var $iconCt = this.getIconElement(),
            iconCls = this.resolveCssClassForComponentType(componentType);
        $iconCt.children('div').attr('class', iconCls);
        $iconCt.attr('title', componentType);
    };


    proto.resolveCssClassForComponentType = function (componentType) {
        var iconCls;

        switch (componentType) {
        case 'page':
            iconCls = 'live-edit-component-menu-page-icon';
            break;

        case 'region':
            iconCls = 'live-edit-component-menu-region-icon';
            break;

        case 'part':
            iconCls = 'live-edit-component-menu-part-icon';
            break;

        case 'content':
            iconCls = 'live-edit-component-menu-content-icon';
            break;

        case 'paragraph':
            iconCls = 'live-edit-component-menu-paragraph-icon';
            break;

        default:
            iconCls = '';
        }

        return iconCls;
    };


    proto.getButtons = function () {
        return this.buttons;
    };


    proto.getIconElement = function () {
        return $('.live-edit-component-menu-title-icon', this.getEl());
    };


    proto.getTitleElement = function () {
        return $('.live-edit-component-menu-title-text', this.getEl());
    };


    proto.getCloseButton = function () {
        return $('.live-edit-component-menu-title-close-button', this.getEl());
    };


    proto.getMenuItemsPlaceholderElement = function () {
        return $('.live-edit-component-menu-items', this.getEl());
    };

}($liveedit));