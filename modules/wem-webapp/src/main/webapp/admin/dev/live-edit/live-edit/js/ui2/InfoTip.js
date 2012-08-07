(function () {
    // Class definition (constructor function)
    var infoTip = AdminLiveEdit.ui2.InfoTip = function () {
        this.create();
        this.registerSubscribers();
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

    p.registerSubscribers = function () {
        var self = this;
        $liveedit.subscribe('/page/component/select', function(event, $component) {
            self.moveToComponent.call(self, event, $component);
        });
        $liveedit.subscribe('/page/component/deselect', function() {
            self.hide.call(self);
        });
    };


    p.create = function () {
        var html = '<div class="live-edit-info-tip" style="top:-5000px; left:-5000px;">' +
                   '    <img src="../live-edit/images/component_blue.png" style="padding-right: 7px; vertical-align: top"/>' + // TODO: Create a class
                   '    <span class="live-edit-info-tip-name-text"><!-- --></span>' +
                   '    <div class="live-edit-info-tip-arrow-border"></div>' +
                   '    <div class="live-edit-info-tip-arrow"></div>' +
                   '</div>';

        this.createElement(html);
        this.appendTo($liveedit('body'));
    };


    p.moveToComponent = function (event, $component) {
        var $infoTip = this.getEl();
        var componentName = util.getNameFromComponent($component);
        var componentType = util.getTypeFromComponent($component);

        // Set text and icon first so position is calculated correctly.
        this.setText(componentName);
        this.setIcon(componentType);

        var componentBoxModel = util.getBoxModel($component);
        var top = componentBoxModel.top - 50;
        var left = componentBoxModel.left + (componentBoxModel.width / 2) - ($infoTip.width() / 2);
        $infoTip.css({
            top: top + 12,
            left: left
        });
    };


    p.setText = function (text) {
        this.getEl().find('.live-edit-info-tip-name-text').text(text);
    };


    p.setIcon = function (componentType) {
        this.getEl().find('img').attr('src', util.getIconForComponent(componentType));
    };


    p.hide = function () {
        this.getEl().css({
            top: '-5000px',
            left: '-5000px'
        });
    }

}());