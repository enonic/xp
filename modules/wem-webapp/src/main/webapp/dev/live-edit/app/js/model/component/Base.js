(function ($) {
    'use strict';

    // Namespaces
    AdminLiveEdit.model.component = {};

    AdminLiveEdit.model.component.Base = function () {
        this.cssSelector = '';
        this.registerGlobalListeners();
    };


    AdminLiveEdit.model.component.Base.prototype = {

        registerGlobalListeners: function () {
        },

        attachMouseOverEvent: function () {
            var me = this;

            $(document).on('mouseover', me.cssSelector, function (event) {

                var $component = $(this);

                var targetIsUiComponent = me.isLiveEditUiComponent($(event.target));
                var cancelEvents = targetIsUiComponent || me.hasComponentSelected() || AdminLiveEdit.DragDropSort.isDragging();
                if (cancelEvents) {
                    return;
                }
                event.stopPropagation();

                $(window).trigger('component:mouseover', [$component]);
            });
        },


        attachMouseOutEvent: function () {
            var me = this;

            $(document).on('mouseout', function () {
                var hasComponentSelected = $('.live-edit-selected-component').length > 0;
                var cancelEvents = me.hasComponentSelected();
                if (cancelEvents) {
                    return;
                }
                $(window).trigger('component:mouseout');
            });
        },


        attachClickEvent: function () {
            var me = this;

            $(document).on('click touchstart', me.cssSelector, function (event) {
                if (me.isLiveEditUiComponent($(event.target))) {
                    return;
                }

                event.stopPropagation();
                event.preventDefault();

                var $component = $(event.currentTarget),
                    componentIsSelected = $component.hasClass('live-edit-selected-component'),
                    pageHasComponentSelected = $('.live-edit-selected-component').length > 0;

                if (componentIsSelected || pageHasComponentSelected) {
                    $(window).trigger('component:click:deselect');
                } else {
                    $(window).trigger('component:click:select', [$component]);
                }
            });
        },


        hasComponentSelected: function () {
            return $('.live-edit-selected-component').length > 0;
        },


        isLiveEditUiComponent: function ($target) {
            return $target.is('[id*=live-edit-ui-cmp]') || $target.parents('[id*=live-edit-ui-cmp]').length > 0;
        },


        getAll: function () {
            return $(this.cssSelector);
        }

    };
}($liveedit));
