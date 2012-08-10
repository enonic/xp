AdminLiveEdit.components.ParagraphComponents = (function () {

    var SELECTOR = '[data-live-edit-type=paragraph]';


    function getAll() {
        return $liveedit(SELECTOR);
    }


    function attachEventListeners() {
        $liveedit(document).on('mouseover', SELECTOR, function (event) {
            var $component = $liveedit(this);
            var componentIsDescendantOfSelected = $component.parents('.live-edit-selected-component').length === 1;
            var disableHover = componentIsDescendantOfSelected || AdminLiveEdit.ui.DragDrop.isDragging();
            if (disableHover) {
                return;
            }
            event.stopPropagation();

            $liveedit.publish('/ui/highlighter/on-highlight', [$component]);
        });

        $liveedit(document).on('mouseout', function (event) {
            $liveedit.publish('/ui/highlighter/on-hide');
        });

        $liveedit(document).on('click touchstart', SELECTOR, function (event) {
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
    }


    function init() {
        attachEventListeners();
    }


    // ***********************************************************************************************************************************//
    // Define public methods

    return {

        SELECTOR: SELECTOR,

        init: function () {
            init();
        },

        getAll: function () {
            return getAll();
        }

    };

}());