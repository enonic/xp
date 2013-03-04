(function ($) {
    'use strict';

    // Namespaces
    AdminLiveEdit.model.component = {};

    AdminLiveEdit.model.component.Base = function () {
        this.cssSelector = '';
        this.cancelEvents = false;
        this.registerGlobalListeners();
    };


    AdminLiveEdit.model.component.Base.prototype = {

        registerGlobalListeners: function () {
            var me = this;

            $(window).on('component:paragraph:edit', function () {
                me.cancelEvents = true;
            });

            $(window).on('shader:click', function () {
                me.cancelEvents = false;
            });
        },

        attachMouseOverEvent: function () {
            var me = this;

            $(document).on('mouseover', me.cssSelector, function (event) {
                if (me.cancelEvents) {
                    return;
                }
                var $component = $(this);

                var targetIsUiComponent = me.isLiveEditUiComponent($(event.target));
                var cancelEvents = targetIsUiComponent || me.hasComponentSelected() || AdminLiveEdit.DragDrop.isDragging();
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
                if (me.cancelEvents) {
                    return;
                }

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
                if (me.cancelEvents) {
                    return;
                }

                if (me.isLiveEditUiComponent($(event.target))) {
                    return;
                }
                event.stopPropagation();
                event.preventDefault();

                var $closestComponentFromTarget = $(event.target).closest('[data-live-edit-type]'),
                    componentIsSelected = $closestComponentFromTarget.hasClass('live-edit-selected-component'),
                    pageHasComponentSelected = $('.live-edit-selected-component').length > 0;

                if (componentIsSelected || pageHasComponentSelected) {
                    $(window).trigger('component:click:deselect');
                } else {
                    $(window).trigger('component:click:select', [$closestComponentFromTarget]);
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
