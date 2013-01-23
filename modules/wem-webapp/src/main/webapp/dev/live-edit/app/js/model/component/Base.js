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
                var pageHasComponentSelected = $('.live-edit-selected-component').length > 0;
                var disableHover = targetIsUiComponent || pageHasComponentSelected || AdminLiveEdit.DragDrop.isDragging();
                if (disableHover) {
                    return;
                }
                event.stopPropagation();

                $(window).trigger('component:mouseover', [$component]);
            });
        },


        attachMouseOutEvent: function () {
            $(document).on('mouseout', function () {
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
                    $(window).trigger('component:deselect');
                } else {
                    var pageHasComponentSelected = $('.live-edit-selected-component').length > 0;
                    if (pageHasComponentSelected) {
                        $(window).trigger('component:deselect');
                    } else {
                        $(window).trigger('component:select', [$closestComponentFromTarget]);
                    }
                }
                // return false;
            });
        },


        isLiveEditUiComponent: function (event) {
            return $(event.target).is('[id*=live-edit-ui-cmp]') || $(event.target).parents('[id*=live-edit-ui-cmp]').length > 0;
        },


        getAll: function () {
            return $(this.cssSelector);
        }

    };
}($liveedit));
