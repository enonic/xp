AdminLiveEdit.components.WindowComponents = (function () {

    var SELECTOR = '[data-live-edit-type=window]';


    function getAll() {
        return $liveedit(SELECTOR);
    }


    function appendEmptyWindowPlaceholder($window) {
        var $placeholder = $liveedit('<div/>', {
            'class': 'live-edit-empty-window-placeholder',
            'html': 'Empty Window'
        });
        $window.append($placeholder);
    }


    function isWindowEmpty($window) {
        return $liveedit($window).children().length === 0;
    }


    function renderPlaceholdersForEmptyWindows() {
        getAll().each(function (index) {
            var $window = $liveedit(this);
            var windowIsEmpty = isWindowEmpty($window);
            if (windowIsEmpty) {
                appendEmptyWindowPlaceholder($window);
            }
        });
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

            $liveedit.publish('/page/component/highlight', [$component]);
        });

        $liveedit(document).on('mouseout', function (event) {
            $liveedit.publish('/page/component/hide-highlighter');
        });

        $liveedit(document).on('click touchstart', SELECTOR, function (event) {
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
    }


    function init() {
        renderPlaceholdersForEmptyWindows();
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