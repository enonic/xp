(function () {
    // Namespaces
    AdminLiveEdit.components = {};
    AdminLiveEdit.components.events = {};


    AdminLiveEdit.components.events.Base = function () {
        this.selector = '';
    };


    AdminLiveEdit.components.events.Base.prototype = {
        attachMouseOverEvent: function () {
            var self = this;

            $liveedit(document).on('mouseover', this.selector, function (event) {
                var $component = $liveedit(this);

                var isUi = $liveedit(event.target).is('.live-edit-info-tip, .live-edit-button') ||
                           $liveedit(event.target).parents('.live-edit-info-tip, .live-edit-button').length > 0;

                var pageHasComponentSelected = $liveedit('.live-edit-selected-component').length > 0;
                var disableHover = isUi || pageHasComponentSelected || AdminLiveEdit.ui.DragDrop.isDragging();
                if (disableHover) {
                    return;
                }
                event.stopPropagation();

                $liveedit.publish('/ui/highlighter/on-highlight', [$component]);
            });
        },


        attachMouseOutEvent: function () {
            $liveedit(document).on('mouseout', function (event) {
                // var $body = $liveedit('body');
                // $body.css('cursor', '');
                // $liveedit.publish('/ui/highlighter/on-hide');
            });
        },


        attachClickEvent: function () {
            $liveedit(document).on('click touchstart', this.selector, function (event) {
                event.stopPropagation();
                event.preventDefault();
                var $closestComponentFromTarget = $liveedit(event.target).closest('[data-live-edit-type]');
                var componentIsSelected = $closestComponentFromTarget.hasClass('live-edit-selected-component');
                if (componentIsSelected) {
                    $liveedit.publish('/ui/componentselector/on-deselect');
                } else {
                    var pageHasComponentSelected = $liveedit('.live-edit-selected-component').length > 0;
                    if (pageHasComponentSelected) {
                        $liveedit.publish('/ui/componentselector/on-deselect');
                    } else {
                        $liveedit.publish('/ui/componentselector/on-select', [$closestComponentFromTarget]);
                    }
                }
                return false;
            });
        },


        getAll: function () {
            return $liveedit(this.selector);
        }

    };
}());
