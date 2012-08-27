(function () {
    'use strict';

    // Class definition (constructor function)
    var componentSelector = AdminLiveEdit.ui.ComponentSelector = function () {
        this.$selectedComponent = $liveedit([]); // Empty jQuery object
        this.create();
        this.bindEvents();
    };

    // Inherits ui.Base
    componentSelector.prototype = new AdminLiveEdit.ui.Base();

    // Fix constructor as it now is Base
    componentSelector.constructor = componentSelector;

    // Shorthand ref to the prototype
    var p = componentSelector.prototype;

    // Uses
    var util = AdminLiveEdit.Util;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    p.bindEvents = function () {
        $liveedit(window).on('component:select', $liveedit.proxy(this.select, this));

        $liveedit(window).on('component:deselect', $liveedit.proxy(this.deselect, this));

        $liveedit(window).on('component:drag:stop', function (event, uiEvent, ui, wasSelectedOnSortStart) {
            if (wasSelectedOnSortStart) {
                $liveedit(window).trigger('component:select', [ui.item]);
            }
        });
    };


    p.create = function () {
        var html = '<svg xmlns="http://www.w3.org/2000/svg" version="1.1" class="live-edit-selected-border">' +
                   '    <rect width="150" height="150"/>' +
                   '</svg>';

        this.createElement(html);
        this.appendTo($liveedit('body'));
    };


    p.getSelected = function () {
        return this.$selectedComponent;
    };


    p.setSelected = function ($component) {
        this.$selectedComponent = $component;
    };


    p.hide = function () {
        var $el = this.getEl();
        $liveedit('body').append(this.getEl());

        $el.css({
            top: '-5000px',
            left: '-5000px'
        });
    };


    p.scrollComponentIntoView = function ($component) {
        var componentTopPosition = util.getPageComponentPagePosition($component).top;
        if (componentTopPosition <= window.pageYOffset) {
            $liveedit('html, body').animate({scrollTop: componentTopPosition - 10}, 200);
        }
    };


    p.select = function (event, $component) {
        var $el = this.getEl();

        // Add position relative to the page component in order have absolute positioned elements inside.
        $liveedit('.live-edit-selected-component').removeClass('live-edit-selected-component');
        $component.addClass('live-edit-selected-component');

        this.setSelected($component);
        this.scrollComponentIntoView($component);
    };


    p.deselect = function () {
        $liveedit('.live-edit-selected-component').removeClass('live-edit-selected-component');
        this.hide();
        this.setSelected($liveedit([]));
    };

}());