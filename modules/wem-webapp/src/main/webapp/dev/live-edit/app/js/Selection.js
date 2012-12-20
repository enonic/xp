(function ($) {
    'use strict';

    // Class definition (constructor function)
    var selection = AdminLiveEdit.Selection = function () {
        this.$selectedComponent = $([]); // Empty jQuery object
        this.bindGlobalEvents();
    };

    // Shorthand ref to the prototype
    var proto = selection.prototype;

    // Uses
    var util = AdminLiveEdit.Util;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    proto.bindGlobalEvents = function () {
        $(window).on('component:select', $.proxy(this.select, this));

        $(window).on('component:deselect', $.proxy(this.deselect, this));

        $(window).on('component:drag:stop', function (event, uiEvent, ui, wasSelectedOnDragStart) {
            if (wasSelectedOnDragStart) {
                $(window).trigger('component:select', [ui.item]);
            }
        });
    };


    proto.getSelected = function () {
        return this.$selectedComponent;
    };


    proto.setSelected = function ($component) {
        this.$selectedComponent = $component;
    };


    proto.scrollComponentIntoView = function ($component) {
        var componentTopPosition = util.getPageComponentPagePosition($component).top;
        if (componentTopPosition <= window.pageYOffset) {
            $('html, body').animate({scrollTop: componentTopPosition - 10}, 200);
        }
    };


    proto.select = function (event, $component) {
        // Add CSS position relative to the page component in order have absolute positioned elements inside.
        $('.live-edit-selected-component').removeClass('live-edit-selected-component');
        $component.addClass('live-edit-selected-component');

        this.setSelected($component);
        this.scrollComponentIntoView($component);
    };


    proto.deselect = function () {
        $('.live-edit-selected-component').removeClass('live-edit-selected-component');
        this.setSelected($([]));
    };

}($liveedit));