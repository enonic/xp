(function ($) {
    'use strict';

    // Namespaces
    AdminLiveEdit.view.componentbar = {};

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
    componentBar.constructor = componentBar;

    // Shorthand ref to the prototype
    var proto = componentBar.prototype;

    // Uses
    // var util = AdminLiveEdit.Util;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    var html = '';
    html += '<div class="live-edit-components-container">';
    html += '    <div class="live-edit-toggle-components-container">';
    html += '        <div class="live-edit-components-toggle-text">Hide components</div>';
    html += '        <div class="live-edit-components">';
    html += '            <div class="live-edit-form-container">';
    html += '               <form>';
    html += '                   <input type="text" placeholder="Filter" name="filter"/>';
    html += '               </form>';
    html += '            </div>';
    html += '            <ul>';
    html += '            </ul>';
    html += '        </div>';
    html += '    </div>';
    html += '</div>';


    proto.getComponentsDataUrl = function () {
        return '../app/data/mock-components.json';
    };


    proto.addView = function () {
        var me = this;

        me.createElement(html);
        me.appendTo($('body'));
    };


    proto.registerGlobalListeners = function () {
        var me = this;
        $(window).on('component:click:select', $.proxy(me.fadeOut, me));
        $(window).on('component:click:deselect', $.proxy(me.fadeIn, me));
        $(window).on('component:drag:start', $.proxy(me.fadeOut, me));
        $(window).on('component:drag:stop', $.proxy(me.fadeIn, me));
        $(window).on('component:sort:stop', $.proxy(me.fadeIn, me));
        $(window).on('component:sort:start', $.proxy(me.fadeOut, me));
        $(window).on('component:sort:update', $.proxy(me.fadeIn, me));
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
            $liveedit(window).trigger('componentBar:mouseover');
        });
    };


    proto.loadComponentsData = function () {
        var me = this;
        $.getJSON(me.getComponentsDataUrl(), null, function (data, textStatus, jqXHR) {
            me.renderComponents(data);
            $(window).trigger('componentBar:dataLoaded');
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
        html += '<li class="live-edit-component" data-live-edit-component-key="' + component.key + '" data-live-edit-component-name="' + component.name + '">';
        html += '    <img src="../app/images/srs.jpeg"/>';
        html += '    <div class="live-edit-component-text">';
        html += '        <span>' + component.name + '</span>';
        html += '        <small>' + component.subtitle + '</small>';
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
        this.getBar().css('right', '0');
    };


    proto.hide = function () {
        this.getBar().css('right', '-200px');
    };


    proto.fadeIn = function (event, triggerConfig) {
        // componenttip/menu.js triggers a component:click:deselect event
        // which results in that the bar is faded in (see the listeners above)
        // The triggerConfig is a temporary workaround until we get this right.
        if (triggerConfig && triggerConfig.showComponentBar === false) {
            return;
        }
        this.getBar().css('opacity', '1');
    };


    proto.fadeOut = function (event) {
        this.getBar().css('opacity', '0');
    };


    proto.getBar = function () {
        return this.getEl();
    };


    proto.getToggle = function () {
        return $('.live-edit-components-toggle-text', this.getEl());
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

}($liveedit));