(function ($) {
    'use strict';

    // Namespaces
    AdminLiveEdit.view.componenttip.menu = {};


    // Class definition (constructor)
    var menu = AdminLiveEdit.view.componenttip.menu.Menu = function () {
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
        /*
        $(window).on('component:click:select', $.proxy(this.show, this));
        */
        $(window).on('component:click:deselect', $.proxy(this.hide, this));
        $(window).on('tip:menu:click', $.proxy(this.toggle, this));
        $(window).on('component:sort:start', $.proxy(this.fadeOutAndHide, this));
        $(window).on('component:remove', $.proxy(this.hide, this));
        $(window).on('component:paragraph:edit:init', $.proxy(this.hide, this));
    };


    proto.addView = function () {
        var me = this;

        me.createElement('<div class="live-edit-component-menu" style="top:-5000px; left:-5000px;"></div>');
        me.appendTo($('body'));
        me.addButtons();
    };


    proto.toggle = function (event, $selectedComponent) {
        var me = this;
        if (me.hidden) {
            me.show(event, $selectedComponent);
        } else {
            me.hide();
        }
    };


    proto.show = function (event, $selectedComponent) {
        var me = this;
        me.getMenuForComponent($selectedComponent);
        me.moveToTip();
        me.getEl().show();
        me.hidden = false;
    };


    proto.fadeOutAndHide = function () {
        var me = this;
        me.getEl().fadeOut(500, function () {
            me.hide();
            $(window).trigger('component:click:deselect', {showComponentBar: false});
        });
    };


    proto.hide = function () {
        var me = this;
        me.getEl().css({ top: '-5000px', left: '-5000px', right: '' });
        me.hidden = true;
    };


    proto.moveToTip = function () {
        var me = this,
            tipElement = me.trigger.getEl(),
            tipOffset = tipElement.offset(),
            height = tipElement.outerHeight(),
            topPos = tipOffset.top + height - 1,
            leftPos = tipOffset.left;

        me.getEl().css({
            top: topPos,
            left: leftPos
        });
    };


    proto.getMenuForComponent = function ($component) {
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
        var settingsButton = new AdminLiveEdit.view.componenttip.menu.SettingsButton(me);
        var detailsButton = new AdminLiveEdit.view.componenttip.menu.DetailsButton(me);
        var insertButton = new AdminLiveEdit.view.componenttip.menu.InsertButton(me);
        var resetButton = new AdminLiveEdit.view.componenttip.menu.ResetButton(me);
        var clearButton = new AdminLiveEdit.view.componenttip.menu.ClearButton(me);
        var viewButton = new AdminLiveEdit.view.componenttip.menu.ViewButton(me);
        var editButton = new AdminLiveEdit.view.componenttip.menu.EditButton(me);
        var removeButton = new AdminLiveEdit.view.componenttip.menu.RemoveButton(me);

        var i;
        for (i = 0; i < me.buttons.length; i++) {
            me.buttons[i].appendTo(me.getEl());
        }
    };


    proto.getButtons = function () {
        return this.buttons;
    };

}($liveedit));