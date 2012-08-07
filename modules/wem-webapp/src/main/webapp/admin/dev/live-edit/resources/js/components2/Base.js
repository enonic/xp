// Namespace
AdminLiveEdit.components2 = {};

AdminLiveEdit.components2.Base = function () {
    this.selector = '';
    this.highlightColor = '#141414';
};

AdminLiveEdit.components2.Base.prototype = {
    attachMouseOverEvent: function () {
        var self = this;
        $liveedit(document).on('mouseover', this.selector, function (event) {
            var $component = $liveedit(this);
            var componentIsDescendantOfSelected = $component.parents('.live-edit-selected-component').length === 1;
            // TODO: Get isDragging
            //var disableHover = componentIsDescendantOfSelected || AdminLiveEdit.ui.DragDrop.isDragging();
            var disableHover = componentIsDescendantOfSelected;
            if (disableHover) {
                return;
            }
            event.stopPropagation();

            $liveedit.publish('/page/component/highlight', [$component, self.highlightColor]);
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
