(function ($) {
    'use strict';

    // Namespaces
    AdminLiveEdit.view.componentbar = {};

    // Class definition (constructor)
    var componentBar = AdminLiveEdit.view.componentbar.ComponentBar = function () {
        var me = this;
        me.addView();
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
    html += '            </ul>';
    html += '        </div>';
    html += '    </div>';
    html += '</div>';


    proto.bindGlobalEvents = function () {
    };


    proto.addView = function () {
        var me = this;

        me.createElement(html);
        me.appendTo($('body'));
    };


    proto.toggle = function () {
    };


    proto.show = function (event, $selectedComponent) {
    };


    proto.hide = function () {
    };

}($liveedit));