(function ($) {
    'use strict';

    // Namespaces
    AdminLiveEdit.model.component = {};

    AdminLiveEdit.model.component.Base = function () {
        this.cssSelector = '';
    };


    AdminLiveEdit.model.component.Base.prototype = {
        attachMouseOverEvent: function () {
            var me = this;

            $(document).on('mouseover', this.cssSelector, function (event) {
                var $component = $(this);

                var targetIsUiComponent = me.isLiveEditUiComponent(event);
                var cancelEvent = targetIsUiComponent || me.hasComponentSelected() || AdminLiveEdit.DragDrop.isDragging();
                if (cancelEvent) {
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
                var cancelEvent = me.hasComponentSelected();
                if (cancelEvent) {
                    return;
                }
                $(window).trigger('component:mouseout');
            });
        },


        attachClickEvent: function () {
            var me = this;

            $(document).on('click touchstart', this.cssSelector, function (event) {
                if (me.isLiveEditUiComponent(event)) {
                    return;
                }
                event.stopPropagation();
                event.preventDefault();
                var $closestComponentFromTarget = $(event.target).closest('[data-live-edit-type]');
                var componentIsSelected = $closestComponentFromTarget.hasClass('live-edit-selected-component');
                if (componentIsSelected) {
                    $(window).trigger('component:click:deselect');
                } else {
                    var pageHasComponentSelected = $('.live-edit-selected-component').length > 0;
                    if (pageHasComponentSelected) {
                        $(window).trigger('component:click:deselect');
                    } else {
                        $(window).trigger('component:click:select', [$closestComponentFromTarget]);
                    }
                }
                // return false;
            });
        },


        hasComponentSelected: function () {
            return $('.live-edit-selected-component').length > 0;
        },


        isLiveEditUiComponent: function (event) {
            return $(event.target).is('[id*=live-edit-ui-cmp]') || $(event.target).parents('[id*=live-edit-ui-cmp]').length > 0;
        },


        getAll: function () {
            return $(this.cssSelector);
        }

    };
}($liveedit));
