(function () {
    // Namespaces
    AdminLiveEdit.page = {};
    AdminLiveEdit.page.components = {};


    AdminLiveEdit.page.components.Base = function () {
        this.selector = '';
        this.highlightColor = '#141414';
    };


    AdminLiveEdit.page.components.Base.prototype = {
        attachMouseOverEvent: function () {
            var self = this;
            $liveedit(document).on('mouseover', this.selector, function (event) {
                var $component = $liveedit(this);
                var componentIsDescendantOfSelected = $component.parents('.live-edit-selected-component').length === 1;
                // TODO: remove reference to DragDrop, use PubSub.
                var disableHover = componentIsDescendantOfSelected || AdminLiveEdit.ui2.DragDrop.isDragging();
                if (disableHover) {
                    return;
                }
                event.stopPropagation();

                $liveedit.publish('/ui/highlighter/on-highlight', [$component, self.highlightColor]);
            });
        },


        attachMouseOutEvent: function () {
            $liveedit(document).on('mouseout', function (event) {
                $liveedit.publish('/ui/highlighter/on-hide');
            });
        },


        attachClickEvent: function () {
            $liveedit(document).on('click touchstart', this.selector, function (event) {
                event.stopPropagation();
                event.preventDefault();
                var $closestComponentFromTarget = $liveedit(event.target).closest('[data-live-edit-type]');
                var componentIsSelected = $closestComponentFromTarget.hasClass('live-edit-selected-component');
                if (componentIsSelected) {
                    $liveedit.publish('/ui/selectedcomponent/on-deselect');
                } else {
                    $liveedit.publish('/ui/selectedcomponent/on-select', [$closestComponentFromTarget]);
                }
                return false;
            });
        },


        getAll: function () {
            return $liveedit(this.selector);
        }

    };
}());
