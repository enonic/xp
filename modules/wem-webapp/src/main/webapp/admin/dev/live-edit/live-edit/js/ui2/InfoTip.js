(function () {
    // Class definition (constructor function)
    var infoTip = AdminLiveEdit.ui2.InfoTip = function () {
        this.create();
    };

    // Inherits ui.Base
    infoTip.prototype = new AdminLiveEdit.ui2.Base();

    // Fix constructor as it now is Base
    infoTip.constructor = infoTip;

    // Shorthand ref to the prototype
    var p = infoTip.prototype;

    // Uses
    var util = AdminLiveEdit.Util;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *


    p.create = function () {
        var html = '<div id="live-edit-info-tip" style="top:-5000px; left:-5000px;">' +
                   '    <img src="../live-edit/images/component_blue.png" style="padding-right: 7px; vertical-align: top"/>' + // TODO: Create a class
                   '    <span id="live-edit-info-tip-name-text"><!-- --></span>' +
                   '    <div id="live-edit-info-tip-arrow-border"></div>' +
                   '    <div id="live-edit-info-tip-arrow"></div>' +
                   '</div>';

        this.createElement(html);
        this.appendTo($liveedit('body'));
    };


    p.registerSubscribers = function () {
        $liveedit.subscribe('/page/component/select', moveToComponent);
        $liveedit.subscribe('/page/component/deselect', hide);
    };


    p.moveToComponent = function (event, $component) {
        var componentName = util.getNameFromComponent($component);
        var componentType = util.getTypeFromComponent($component);
        var componentBoxModel = util.getBoxModel($component);
        var top = componentBoxModel.top - 50;
        var left = componentBoxModel.left + (componentBoxModel.width / 2) - ($infoTip.outerWidth() / 2);

        p.updateText(componentName);
        p.updateIcon(componentType);

        this.getEl().css({
            top: top + 12,
            left: left
        });
    };


    p.updateText = function (text) {
        this.getEl().find('.live-edit-info-tip-name-text').text(text);
    };


    p.updateIcon = function (componentType) {
        this.getEl().find('img').attr('src', util.getIconForComponent(componentType));
    };


    p.hide = function () {
        this.getEl().css({
            top: '-5000px',
            left: '-5000px'
        });
    }

}());