(function ($) {
    'use strict';

    // Namespaces
    AdminLiveEdit.view.componentbar = {};

    // Class definition (constructor)
    var componentBar = AdminLiveEdit.view.componentbar.ComponentBar = function () {
        var me = this;

        me.hidden = false;

        me.addView();

        me.loadComponentsData();

        me.addEvents();
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
    html += '                   <input type="text" placeholder="Search" name="search"/>';
    html += '               </form>';
    html += '            </div>';
    html += '            <ul>';
/*
    html += '                <li class="live-edit-component-list-header">';
    html += '                    <span>Layouts</span>';
    html += '                </li>';
    html += '                <li class="live-edit-component">';
    html += '                    <img src="../app/images/srs.jpeg"/>';
    html += '                    <div class="live-edit-component-text">';
    html += '                        <span>Column layouts</span>';
    html += '                        <small>Subtitle</small>';
    html += '                    </div>';
    html += '                </li>';
    html += '                <li class="live-edit-component">';
    html += '                    <img src="../app/images/srs.jpeg"/>';
    html += '                    <div class="live-edit-component-text">';
    html += '                        <span>Spacer</span>';
    html += '                        <small>Subtitle</small>';
    html += '                    </div>';
    html += '                </li>';
    html += '                <li class="live-edit-component-list-header">';
    html += '                    <span>Advanced</span>';
    html += '                </li>';
    html += '                <li class="live-edit-component">';
    html += '                    <img src="../app/images/srs.jpeg"/>';
    html += '                    <div class="live-edit-component-text">';
    html += '                        <span>Article List</span>';
    html += '                        <small>Subtitle</small>';
    html += '                    </div>';
    html += '                </li>';
    html += '                <li class="live-edit-component">';
    html += '                    <img src="../app/images/srs.jpeg"/>';
    html += '                    <div class="live-edit-component-text">';
    html += '                        <span>Article Show</span>';
    html += '                        <small>Very loooooooooooong subtitle</small>';
    html += '                    </div>';
    html += '                </li>';
*/
    html += '            </ul>';
    html += '        </div>';
    html += '    </div>';
    html += '</div>';


    proto.addView = function () {
        var me = this;

        me.createElement(html);
        me.appendTo($('body'));
    };


    proto.bindGlobalEvents = function () {
    };


    proto.getComponentsDataUrl = function () {
        return '../app/data/mock_components.json';
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
            componentTypes = jsonData.componentTypes;

        $.each(componentTypes, function (index, componentType) {
            me.addHeader(componentType);
            if (componentType.components) {
                me.addComponentsToGroup(componentType.components)
            }
        });
    };


    proto.addHeader = function (componentType) {
        var me = this,
            html = '';
        html += '<li class="live-edit-component-list-header">';
        html += '    <span>' + componentType.name + '</span>';
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
        html += '<li class="live-edit-component" data-live-edit-component-key="' + component.key + '">';
        html += '    <img src="../app/images/srs.jpeg"/>';
        html += '    <div class="live-edit-component-text">';
        html += '        <span>' + component.name + '</span>';
        html += '        <small>' + component.subtitle + '</small>';
        html += '    </div>';
        html += '</li>';

        me.getComponentsContainer().append(html);
    };


    proto.addEvents = function () {
        var me = this;
        me.addToggleEvent();
        me.getBar().on('mouseover', function () {
            $liveedit(window).trigger('componentBar:mouseover');
        });
    };


    proto.addToggleEvent = function () {
        var me = this;
        me.getToggle().click(function (event) {
            me.toggle();
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
        var me = this;
        me.getBar().css('right', '-' + me.getComponentsContainer().width() + 'px');
    };


    proto.getBar = function () {
        return this.getEl();
    };


    proto.getToggle = function () {
        return $('.live-edit-components-toggle-text', this.getEl());
    };


    proto.getComponentsContainer = function () {
        return $('.live-edit-components ul', this.getEl());
    };

}($liveedit));