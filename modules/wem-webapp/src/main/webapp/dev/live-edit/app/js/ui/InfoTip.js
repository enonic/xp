(function () {
    'use strict';

    // Class definition (constructor function)
    var infoTip = AdminLiveEdit.ui.InfoTip = function () {
        this.create();
        this.bindEvents();
    };

    // Inherits ui.Base
    infoTip.prototype = new AdminLiveEdit.ui.Base();

    // Fix constructor as it now is Base
    infoTip.constructor = infoTip;

    // Shorthand ref to the prototype
    var p = infoTip.prototype;

    // Uses
    var util = AdminLiveEdit.Util;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    p.bindEvents = function () {
        $liveedit(window).on('/component/on-select', $liveedit.proxy(this.show, this));

        $liveedit(window).on('/component/on-deselect', $liveedit.proxy(this.hide, this));
    };


    p.create = function () {
        var html = '<div class="live-edit-info-tip" style="top:-5000px; left:-5000px;">' +
                   '    <img src="' + this.blankImage + '" style="padding-right: 7px; vertical-align: top"/>' + // TODO: Create a class
                   '    <span class="live-edit-info-tip-name-text"><!-- --></span>' +
                   '    <div class="live-edit-info-tip-arrow-border"></div>' +
                   '    <div class="live-edit-info-tip-arrow"></div>' +
                   '</div>';

        this.createElement(html);
        this.appendTo($liveedit('body'));

        // Make sure component is not deselected when the infotip element is clicked.
        this.getEl().on('click', function (event) {
            event.stopPropagation();
        });
    };


    p.show = function (event, $component) {
        var self = this;

        var componentName = util.getComponentName($component);
        var componentType = util.getComponentType($component);
        var componentTagName = util.getTagNameForComponent($component);

        // Set text and icon first so position is calculated correctly.
        self.setText(componentName);
        self.setIcon(componentType);

        var componentBoxModel = util.getBoxModel($component);
        var leftPos = componentBoxModel.left + (componentBoxModel.width / 2) - (self.getEl().width() / 2);
        var topPos = componentBoxModel.top - 38;

        if (componentType === 'page' && componentTagName === 'body') {
            topPos = 0;
            self.hideArrows(true);
        } else {
            self.hideArrows(false);
        }

        self.setCssPosition($component);
        self.getEl().css({
            top: topPos,
            left: leftPos
        });
    };


    p.setText = function (text) {
        this.getEl().find('.live-edit-info-tip-name-text').text(text);
    };


    p.setIcon = function (componentType) {
        this.getEl().find('img').attr('src', util.getIconForComponent(componentType));
    };


    p.hideArrows = function (hide) {
        var $arrowElements = this.getEl().children('div[class*="live-edit-info-tip-arrow"]');
        if (hide) {
            $arrowElements.hide();
        } else {
            $arrowElements.show();
        }
    };


    p.hide = function () {
        this.getEl().css({
            top: '-5000px',
            left: '-5000px'
        });
    };

}());