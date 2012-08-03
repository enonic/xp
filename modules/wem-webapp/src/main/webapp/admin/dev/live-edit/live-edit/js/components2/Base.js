// Namespace
AdminLiveEdit.components = {};

AdminLiveEdit.components.Base = function (selector) {
    this.selector = selector;
};

AdminLiveEdit.components.Base.prototype = {
    attachMouseOverEvent: function () {
        $liveedit(document).on('mouseover', this.selector, function (event) {
            var $component = $liveedit(this);
            var componentIsDescendantOfSelected = $component.parents('.live-edit-selected-component').length === 1;
            var disableHover = componentIsDescendantOfSelected || AdminLiveEdit.ui.DragDrop.isDragging();
            if (disableHover) {
                return;
            }
            event.stopPropagation();

            $liveedit.publish('/page/component/highlight', [$component]);
        });
    },

    attachMouseOutEvent: function () {
        $liveedit(document).on('mouseout', function (event) {
            $liveedit.publish('/page/component/hide-highlighter');
        });
    },

    attachClickEvent: function () {
        $liveedit(document).on('click touchstart', this.selector, function (event) {
            event.stopPropagation();
            event.preventDefault();
            var $closestComponentFromTarget = $liveedit(event.target).closest('[data-live-edit-type]');
            var componentIsSelected = $closestComponentFromTarget.hasClass('live-edit-selected-component');
            if (componentIsSelected) {
                $liveedit.publish('/page/component/deselect');
            } else {
                $liveedit.publish('/page/component/select', [$closestComponentFromTarget]);
            }
            return false;
        });
    },


    getAll: function () {
        return $liveedit(this.selector);
    }
};
