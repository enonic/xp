module liveedit.ui {
    var $ = $liveedit;
    var componentHelper = liveedit.ComponentHelper;

    export class Menu extends liveedit.ui.Base {
        private selectedComponent:JQuery;
        private previousPageSizes = null;
        private previousPagePositions = null;
        private hidden = true;
        private buttons = [];
        private buttonConfig = {
            'page':         ['settings', 'reset'],
            'region':       ['parent', 'settings', 'reset', 'clear'],
            'layout':       ['parent', 'settings', 'remove'],
            'part':         ['parent', 'settings', 'details', 'remove'],
            'content':      ['parent', 'opencontent', 'view'],
            'paragraph':    ['parent', 'edit', 'remove']
        };

        constructor() {
            super();
            this.addView();
            this.registerEvents();
            this.registerGlobalListeners();

            console.log('Menu instantiated. Using jQuery ' + $().jquery);
        }


        registerGlobalListeners() {
            $(window).on('component.onSelect', $.proxy(this.show, this));
            $(window).on('component.onDeselect', $.proxy(this.hide, this));
            $(window).on('component.onSortStart', $.proxy(this.fadeOutAndHide, this));
            $(window).on('component.onRemove', $.proxy(this.hide, this));
            $(window).on('component.onParagraphEdit', $.proxy(this.hide, this));
        }


        addView() {
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

            this.createElement(html);
            this.appendTo($('body'));
            this.addButtons();
        }


        registerEvents() {
            var me = this;

            me.getEl().draggable({
                handle: '.live-edit-component-menu-title-bar',
                addClasses: false
            });

            me.getCloseButton().click(function () {
                $(window).trigger('component.onDeselect');
            });
        }


        show(event, $component, pagePosition) {
            var me = this;

            me.selectedComponent = $component;
            me.previousPagePositions = pagePosition;
            me.previousPageSizes = liveedit.DomHelper.getViewPortSize();

            me.updateTitleBar($component);

            me.updateMenuItemsForComponent($component);

            var pageXPosition = pagePosition.x - me.getEl().width() / 2,
                pageYPosition = pagePosition.y + 15;
            me.moveToXY(pageXPosition, pageYPosition);

            me.getEl().show();

            this.hidden = false;
        }


        hide() {
            this.selectedComponent = null;
            this.getEl().hide();
            this.hidden = true;
        }


        fadeOutAndHide() {
            var me = this;
            me.getEl().fadeOut(500, function () {
                me.hide();
                $(window).trigger('component.onDeselect', {showComponentBar: false});
            });
            me.selectedComponent = null;
        }


        moveToXY(x, y) {
            this.getEl().css({
                left: x,
                top: y
            });
        }


        addButtons() {
            var me = this;
            var parentButton = new liveedit.ui.ParentButton(me);
            var settingsButton = new liveedit.ui.SettingsButton(me);
            var detailsButton = new liveedit.ui.DetailsButton(me);
            var insertButton = new liveedit.ui.InsertButton(me);
            var resetButton = new liveedit.ui.ResetButton(me);
            var clearButton = new liveedit.ui.ClearButton(me);
            var openContentButton = new liveedit.ui.OpenContentButton(me);
            var viewButton = new liveedit.ui.ViewButton(me);
            var editButton = new liveedit.ui.EditButton(me);
            var removeButton = new liveedit.ui.RemoveButton(me);

            var i,
                $menuItemsPlaceholder = me.getMenuItemsPlaceholderElement();
            for (i = 0; i < me.buttons.length; i++) {
                me.buttons[i].appendTo($menuItemsPlaceholder);
            }
        }


        updateMenuItemsForComponent($component) {
            var componentType = componentHelper.getComponentType($component);
            var buttonArray = this.getConfigForButton(componentType);
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


        updateTitleBar($component) {
            var componentInfo = componentHelper.getComponentInfo($component);
            this.setIcon(componentInfo.type);
            this.setTitle(componentInfo.name);
        }


        setTitle(titleText) {
            this.getTitleElement().text(titleText);
        }


        setIcon(componentType) {
            var $iconCt = this.getIconElement(),
                iconCls = this.resolveCssClassForComponentType(componentType);
            $iconCt.children('div').attr('class', iconCls);
            $iconCt.attr('title', componentType);
        }


        resolveCssClassForComponentType(componentType) {
            var iconCls;

            switch (componentType) {
                case 'page':
                    iconCls = 'live-edit-component-menu-page-icon';
                    break;

                case 'region':
                    iconCls = 'live-edit-component-menu-region-icon';
                    break;

                case 'layout':
                    iconCls = 'live-edit-component-menu-layout-icon';
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
        }


        getButtons() {
            return this.buttons;
        }


        getConfigForButton(componentType) {
            return this.buttonConfig[componentType];
        }


        getIconElement() {
            return $('.live-edit-component-menu-title-icon', this.getEl());
        }


        getTitleElement() {
            return $('.live-edit-component-menu-title-text', this.getEl());
        }


        getCloseButton() {
            return $('.live-edit-component-menu-title-close-button', this.getEl());
        }


        getMenuItemsPlaceholderElement() {
            return $('.live-edit-component-menu-items', this.getEl());
        }


        handleWindowResize (event) {
        if (this.selectedComponent) {
            var x = this.previousPagePositions.x,
                y = this.previousPagePositions.y;

            x = x - (this.previousPageSizes.width - liveedit.DomHelper.getViewPortSize().width);

            this.moveToXY(x, y);
        }
    }

    }
}

/*
AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.view.menu');

(function ($) {
    'use strict';

    // Class definition (constructor)
    var menu = AdminLiveEdit.view.menu.Menu = function () {
        var me = this;
        me.selectedComponent = null;
        me.previousPageSizes = null;
        me.previousPagePositions = null;
        me.hidden = true;
        me.buttons = [];

        me.buttonConfig = {
            'page': ['settings', 'reset'],
            'region': ['parent', 'settings', 'reset', 'clear'],
            'layout': ['parent', 'settings', 'remove'],
            'part': ['parent', 'settings', 'details', 'remove'],
            'content': ['parent', 'opencontent', 'view'],
            'paragraph': ['parent', 'edit', 'remove']
        };

        me.addView();

        me.registerEvents();

        me.registerGlobalListeners();
    };


    // Inherits Base.js
    menu.prototype = new AdminLiveEdit.view.Base();

    // Fix constructor as it now is Base
    // menu.constructor = menu;

    // Shorthand ref to the prototype
    var proto = menu.prototype;

    // Uses
    var componentHelper = liveedit.ComponentHelper;


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
        $(window).on('component.onDeselect', $.proxy(this.hide, this));
        $(window).on('component.onSortStart', $.proxy(this.fadeOutAndHide, this));
        $(window).on('component.onRemove', $.proxy(this.hide, this));
        $(window).on('component.onParagraphEdit', $.proxy(this.hide, this));
        // $(window).on('liveEdit.onWindowResize', $.proxy(this.handleWindowResize, this));
    };


    proto.addView = function () {
        var me = this;

        me.createElement(html);
        me.appendTo($('body'));
        me.addButtons();
    };


    proto.registerEvents = function () {
        var me = this;

        me.getEl().draggable({
            handle: '.live-edit-component-menu-title-bar',
            addClasses: false
        });

        me.getCloseButton().click(function () {
            $(window).trigger('component.onDeselect');
        });
    };


    proto.show = function (event, $component, pagePosition) {
        var me = this,
            componentInfo = componentHelper.getComponentInfo($component);

        me.selectedComponent = $component;
        me.previousPagePositions = pagePosition;
        me.previousPageSizes = liveedit.DomHelper.getViewPortSize();

        me.updateTitleBar($component);

        me.updateMenuItemsForComponent($component);

        var pageXPosition = pagePosition.x - me.getEl().width() / 2,
            pageYPosition = pagePosition.y + 15;
        me.moveToXY(pageXPosition, pageYPosition);

        me.getEl().show();

        this.hidden = false;
    };


    proto.hide = function () {
        this.selectedComponent = null;
        this.getEl().hide();
        this.hidden = true;
    };


    proto.fadeOutAndHide = function () {
        var me = this;
        me.getEl().fadeOut(500, function () {
            me.hide();
            $(window).trigger('component.onDeselect', {showComponentBar: false});
        });
        me.selectedComponent = null;
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
        var openContentButton = new AdminLiveEdit.view.menu.OpenContentButton(me);
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
        var componentType = componentHelper.getComponentType($component);
        if (this.buttonConfig.hasOwnProperty(componentType)) {
            var buttonArray = this.getConfigForButton(componentType);
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
        var componentInfo = componentHelper.getComponentInfo($component);
        this.setIcon(componentInfo.type);
        this.setTitle(componentInfo.name);
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

        case 'layout':
            iconCls = 'live-edit-component-menu-layout-icon';
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


    proto.getConfigForButton = function (componentType) {
        return this.buttonConfig[componentType];
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


    proto.handleWindowResize = function (event) {
        if (this.selectedComponent) {
            var x = this.previousPagePositions.x,
                y = this.previousPagePositions.y;

            x = x - (this.previousPageSizes.width - liveedit.DomHelper.getViewPortSize().width);

            this.moveToXY(x, y);
        }
    };

}($liveedit));
*/