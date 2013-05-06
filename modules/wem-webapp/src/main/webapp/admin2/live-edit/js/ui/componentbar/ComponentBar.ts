AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.view.componentbar');

(function ($) {
    'use strict';

    // Namespaces
    AdminLiveEdit.view.componentbar = {};

    // Constants
    var BAR_WIDTH = 235;
    var TOGGLE_WIDTH = 30;
    var INNER_WIDTH = BAR_WIDTH - TOGGLE_WIDTH;


    // Class definition (constructor)
    var componentBar = AdminLiveEdit.view.componentbar.ComponentBar = function () {
        var me = this;

        me.hidden = true;

        me.addView();

        me.loadComponentsData();

        me.registerGlobalListeners();

        me.registerEvents();
    };


    // Inherits Base.js
    componentBar.prototype = new AdminLiveEdit.view.Base();

    // Fix constructor as it now is Base
    // componentBar.constructor = componentBar;

    // Shorthand ref to the prototype
    var proto = componentBar.prototype;

    // Uses
    // var util = liveedit.Helper;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    var html = '';
    html += '<div class="live-edit-components-container live-edit-collapsed" style="width:' + BAR_WIDTH + 'px; right: -' + INNER_WIDTH + 'px">';
    html += '    <div class="live-edit-toggle-components-container" style="width:' + TOGGLE_WIDTH + 'px"><span class="live-edit-toggle-text-container">Toolbar</span></div>';
    html += '        <div class="live-edit-components">';
    html += '            <div class="live-edit-form-container">';
    html += '               <form onsubmit="return false;">';
    html += '                   <input type="text" placeholder="Filter" name="filter"/>';
    html += '               </form>';
    html += '            </div>';
    html += '            <ul>';
    html += '            </ul>';
    html += '        </div>';
    html += '    </div>';
    html += '</div>';


    proto.getComponentsDataUrl = function () {
        return '../../../admin2/live-edit/data/mock-components.json';
    };


    proto.addView = function () {
        var me = this;

        me.createElement(html);
        me.appendTo($('body'));
    };


    proto.registerGlobalListeners = function () {
        var me = this;
        $(window).on('component.onSelect', $.proxy(me.fadeOut, me));
        $(window).on('component.onDeselect', $.proxy(me.fadeIn, me));
        $(window).on('component.onDragStart', $.proxy(me.fadeOut, me));
        $(window).on('component.onDragStop', $.proxy(me.fadeIn, me));
        $(window).on('component.onSortStop', $.proxy(me.fadeIn, me));
        $(window).on('component.onSortStart', $.proxy(me.fadeOut, me));
        $(window).on('component.onSortUpdate', $.proxy(me.fadeIn, me));
        $(window).on('component.onRemove', $.proxy(me.fadeIn, me));
    };


    proto.registerEvents = function () {
        var me = this;

        me.getToggle().click(function () {
            me.toggle();
        });

        me.getFilterInput().on('keyup', function () {
            me.filterList($(this).val());
        });

        me.getBar().on('mouseover', function () {
            $(window).trigger('componentBar:mouseover');
        });
    };


    proto.loadComponentsData = function () {
        var me = this;
        $.getJSON(me.getComponentsDataUrl(), null, function (data, textStatus, jqXHR) {
            me.renderComponents(data);
            $(window).trigger('componentBar.dataLoaded');
        });
    };


    proto.renderComponents = function (jsonData) {
        var me = this,
            $container = me.getComponentsContainer(),
            groups = jsonData.componentGroups;

        $.each(groups, function (index, group) {
            me.addHeader(group);
            if (group.components) {
                me.addComponentsToGroup(group.components)
            }
        });
    };


    proto.addHeader = function (componentGroup) {
        var me = this,
            html = '';
        html += '<li class="live-edit-component-list-header">';
        html += '    <span>' + componentGroup.name + '</span>';
        html += '</li>';

        me.getComponentsContainer().append(html);
    };


    proto.addComponentsToGroup = function (components) {
        var me = this;
        $.each(components, function (index, component) {
            me.addComponent(component);
        });
    };


    proto.addComponent = function (component) {
        var me = this,
            html = '';
        html += '<li class="live-edit-component" data-live-edit-component-key="' + component.key + '" data-live-edit-component-name="' + component.name + '" data-live-edit-component-type="' + component.type + '">';
        html += '    <img src="' + component.icon + '"/>';
        html += '    <div class="live-edit-component-text">';
        html += '        <div class="live-edit-component-text-name">' + component.name + '</div>';
        html += '        <div class="live-edit-component-text-subtitle">' + component.subtitle + '</div>';
        html += '    </div>';
        html += '</li>';

        me.getComponentsContainer().append(html);
    };


    proto.filterList = function (value) {
        var me = this,
            $element,
            name,
            valueLowerCased = value.toLowerCase();
        me.getComponentList().each(function (index) {
            $element = $(this);
            name = $element.data('live-edit-component-name').toLowerCase();
            $element.css('display', name.indexOf(valueLowerCased) > -1 ? '' : 'none');
        });
    };


    proto.toggle = function () {
        var me = this;
        if (me.hidden) {
            me.show();
            me.hidden = false;
        } else {
            me.hide();
            me.hidden = true;
        }
    };


    proto.show = function () {
        var me = this;
        var $bar = me.getBar();
        $bar.css('right', '0');
        me.getToggleTextContainer().text('');
        $bar.removeClass('live-edit-collapsed');
    };


    proto.hide = function () {
        var me = this;
        var $bar = me.getBar();
        $bar.css('right', '-' + INNER_WIDTH + 'px');
        me.getToggleTextContainer().text('Toolbar');
        $bar.addClass('live-edit-collapsed');
    };


    proto.fadeIn = function (event, triggerConfig) {
        // componenttip/menu.js triggers a component.onDeselect event
        // which results in that the bar is faded in (see the listeners above)
        // The triggerConfig is a temporary workaround until we get this right.
        if (triggerConfig && triggerConfig.showComponentBar === false) {
            return;
        }
        this.getBar().fadeIn(120);
    };


    proto.fadeOut = function (event) {
        this.getBar().fadeOut(120);
    };


    proto.getBar = function () {
        return this.getEl();
    };


    proto.getToggle = function () {
        return $('.live-edit-toggle-components-container', this.getEl());
    };


    proto.getFilterInput = function () {
        return $('.live-edit-form-container input[name=filter]', this.getEl());
    };


    proto.getComponentsContainer = function () {
        return $('.live-edit-components ul', this.getEl());
    };


    proto.getComponentList = function () {
        return $('.live-edit-component', this.getEl());
    };


    proto.getToggleTextContainer = function () {
        return $('.live-edit-toggle-text-container', this.getEl());
    };

}($liveedit));