(function () {
    'use strict';

    // Class definition (constructor function)
    var selection = AdminLiveEdit.Selection = function () {
        this.$selectedComponent = $liveedit([]); // Empty jQuery object
        this.bindEvents();
    };

    // Shorthand ref to the prototype
    var p = selection.prototype;

    // Uses
    var util = AdminLiveEdit.Util;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    p.bindEvents = function () {
        $liveedit(window).on('component:select', $liveedit.proxy(this.select, this));

        $liveedit(window).on('component:deselect', $liveedit.proxy(this.deselect, this));

        $liveedit(window).on('component:drag:stop', function (event, uiEvent, ui, wasSelectedOnDragStart) {
            if (wasSelectedOnDragStart) {
                $liveedit(window).trigger('component:select', [ui.item]);
            }
        });
    };


    p.getSelected = function () {
        return this.$selectedComponent;
    };


    p.setSelected = function ($component) {
        this.$selectedComponent = $component;
    };


    p.scrollComponentIntoView = function ($component) {
        var componentTopPosition = util.getPageComponentPagePosition($component).top;
        if (componentTopPosition <= window.pageYOffset) {
            $liveedit('html, body').animate({scrollTop: componentTopPosition - 10}, 200);
        }
    };


    p.select = function (event, $component) {
        // Add CSS position relative to the page component in order have absolute positioned elements inside.
        $liveedit('.live-edit-selected-component').removeClass('live-edit-selected-component');
        $component.addClass('live-edit-selected-component');

        this.setSelected($component);
        this.scrollComponentIntoView($component);
    };


    p.deselect = function () {
        $liveedit('.live-edit-selected-component').removeClass('live-edit-selected-component');
        this.setSelected($liveedit([]));
    };

}());