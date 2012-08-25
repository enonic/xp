(function () {
    'use strict';

    // Namespaces
    AdminLiveEdit.components = {};

    AdminLiveEdit.components.Base = function () {
        this.cssSelector = '';
    };


    AdminLiveEdit.components.Base.prototype = {
        attachMouseOverEvent: function () {
            var self = this;

            $liveedit(document).on('mouseover', this.cssSelector, function (event) {
                var $component = $liveedit(this);

                var targetIsUiComponent = $liveedit(event.target).is('[id*=live-edit-ui-cmp]') ||
                           $liveedit(event.target).parents('[id*=live-edit-ui-cmp]').length > 0;

                var pageHasComponentSelected = $liveedit('.live-edit-selected-component').length > 0;
                var disableHover = targetIsUiComponent || pageHasComponentSelected || AdminLiveEdit.ui.DragDrop.isDragging();
                if (disableHover) {
                    return;
                }
                event.stopPropagation();

                $liveedit(window).trigger('/component/on-mouse-over', [$component]);
            });
        },


        attachMouseOutEvent: function () {
            $liveedit(document).on('mouseout', function () {
                $liveedit(window).trigger('/component/on-mouse-out');
            });
        },


        attachClickEvent: function () {
            $liveedit(document).on('click touchstart', this.cssSelector, function (event) {
                event.stopPropagation();
                event.preventDefault();
                var $closestComponentFromTarget = $liveedit(event.target).closest('[data-live-edit-type]');
                var componentIsSelected = $closestComponentFromTarget.hasClass('live-edit-selected-component');
                if (componentIsSelected) {
                    $liveedit(window).trigger('/component/on-deselect');
                } else {
                    var pageHasComponentSelected = $liveedit('.live-edit-selected-component').length > 0;
                    if (pageHasComponentSelected) {
                        $liveedit(window).trigger('/component/on-deselect');
                    } else {
                        $liveedit(window).trigger('/component/on-select', [$closestComponentFromTarget]);
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
