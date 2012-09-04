(function () {
    'use strict';

    // Namespaces
    AdminLiveEdit.model.component = {};

    AdminLiveEdit.model.component.Base = function () {
        this.cssSelector = '';
    };


    AdminLiveEdit.model.component.Base.prototype = {
        attachMouseOverEvent: function () {
            var self = this;

            $liveedit(document).on('mouseover', this.cssSelector, function (event) {
                var $component = $liveedit(this);

                var targetIsUiComponent = $liveedit(event.target).is('[id*=live-edit-ui-cmp]') ||
                           $liveedit(event.target).parents('[id*=live-edit-ui-cmp]').length > 0;

                var pageHasComponentSelected = $liveedit('.live-edit-selected-component').length > 0;
                var disableHover = targetIsUiComponent || pageHasComponentSelected || AdminLiveEdit.DragDrop.isDragging();
                if (disableHover) {
                    return;
                }
                event.stopPropagation();

                $liveedit(window).trigger('component:mouseover', [$component]);
            });
        },


        attachMouseOutEvent: function () {
            $liveedit(document).on('mouseout', function () {
                $liveedit(window).trigger('component:mouseout');
            });
        },


        attachClickEvent: function () {
            $liveedit(document).on('click touchstart', this.cssSelector, function (event) {
                event.stopPropagation();
                event.preventDefault();
                var $closestComponentFromTarget = $liveedit(event.target).closest('[data-live-edit-type]');
                var componentIsSelected = $closestComponentFromTarget.hasClass('live-edit-selected-component');
                if (componentIsSelected) {
                    $liveedit(window).trigger('component:deselect');
                } else {
                    var pageHasComponentSelected = $liveedit('.live-edit-selected-component').length > 0;
                    if (pageHasComponentSelected) {
                        $liveedit(window).trigger('component:deselect');
                    } else {
                        $liveedit(window).trigger('component:select', [$closestComponentFromTarget]);
                    }
                }
                return false;
            });
        },


        getAll: function () {
            return $liveedit(this.cssSelector);
        }

    };
}());
