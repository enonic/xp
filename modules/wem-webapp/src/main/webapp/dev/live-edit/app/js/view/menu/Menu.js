(function ($) {
    'use strict';

    // Namespaces
    AdminLiveEdit.view.menu = {};


    // Class definition (constructor)
    var menu = AdminLiveEdit.view.menu.Menu = function () {
        var me = this;
        me.hidden = true;
        me.buttons = [];

        me.buttonConfig = {
            'page': ['settings', 'reset'],
            'region': ['settings', 'reset', 'clear'],
            'part': ['settings', 'details', 'remove'],
            'content': ['view', 'edit'],
            'paragraph': ['edit']
        };

        me.addView();
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

    proto.registerGlobalListeners = function () {
        $(window).on('component:click:select', $.proxy(this.show, this));
        $(window).on('component:click:deselect', $.proxy(this.hide, this));
        $(window).on('component:sort:start', $.proxy(this.fadeOutAndHide, this));
        $(window).on('component:remove', $.proxy(this.hide, this));
        $(window).on('component:paragraph:edit:init', $.proxy(this.hide, this));
    };


    proto.addView = function () {
        var me = this;

        me.createElement('<div class="live-edit-component-menu" style="display: none"></div>');
        me.appendTo($('body'));
        me.addButtons();
    };


    proto.show = function (event, $component, coordinates) {
        var me = this;

        me.updateMenuItemsForComponent($component);

        // Menu should not move on scrolling for a page selection.
        /*
        if (util.getComponentType($component) === 'page') {
            me.getEl().css('position', 'fixed');
            me.moveToXY(coordinates.x, coordinates.y - $('body').scrollTop());

        } else {
            me.getEl().css('position', '');
            me.moveToXY(coordinates.x, coordinates.y);
        }*/

        me.moveToXY(coordinates.x, coordinates.y);

        me.getEl().show();

        this.hidden = false;
    };


    proto.hide = function () {
        this.getEl().css({ top: '-5000px', left: '-5000px', right: '' });
        this.hidden = true;
    };


    proto.fadeOutAndHide = function () {
        var me = this;
        me.getEl().fadeOut(500, function () {
            me.hide();
            $(window).trigger('component:click:deselect', {showComponentBar: false});
        });
    };


    proto.moveToXY = function (x, y) {
        this.getEl().css({
            left: x,
            top: y
        });
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


    proto.addButtons = function () {
        var me = this;
        var settingsButton = new AdminLiveEdit.view.menu.SettingsButton(me);
        var detailsButton = new AdminLiveEdit.view.menu.DetailsButton(me);
        var insertButton = new AdminLiveEdit.view.menu.InsertButton(me);
        var resetButton = new AdminLiveEdit.view.menu.ResetButton(me);
        var clearButton = new AdminLiveEdit.view.menu.ClearButton(me);
        var viewButton = new AdminLiveEdit.view.menu.ViewButton(me);
        var editButton = new AdminLiveEdit.view.menu.EditButton(me);
        var removeButton = new AdminLiveEdit.view.menu.RemoveButton(me);

        var i;
        for (i = 0; i < me.buttons.length; i++) {
            me.buttons[i].appendTo(me.getEl());
        }
    };


    proto.getButtons = function () {
        return this.buttons;
    };

}($liveedit));