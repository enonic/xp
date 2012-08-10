AdminLiveEdit.components.RegionComponents = (function () {

    var SELECTOR = '[data-live-edit-type=region]';


    function getAll() {
        return $liveedit(SELECTOR);
    }


    function appendEmptyRegionPlaceholder($region) {
        var $placeholder = $liveedit('<div/>', {
            'class': 'live-edit-empty-region-placeholder',
            'html': 'Drag components here'
        });
        $region.append($placeholder);
    }


    function isRegionEmpty(region) {
        var hasNotWindows = region.children(AdminLiveEdit.components.WindowComponents.SELECTOR + ':not(:hidden)').length === 0;
        var hasNotDropTargetPlaceholder = region.children('.live-edit-drop-target-placeholder').length === 0;

        return hasNotWindows && hasNotDropTargetPlaceholder;
    }


    function removeAllEmptyRegionPlaceholders() {
        $liveedit('.live-edit-empty-region-placeholder').remove();
    }


    function renderPlaceholdersForEmptyRegions() {
        removeAllEmptyRegionPlaceholders();
        var $regions = getAll();
        $regions.each(function (index) {
            var $region = $liveedit(this);
            var regionIsEmpty = isRegionEmpty($region);
            if (regionIsEmpty) {
                appendEmptyRegionPlaceholder($region);
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


    function registerSubscribers() {
        $liveedit.subscribe('/ui/dragdrop/on-sortupdate', renderPlaceholdersForEmptyRegions);
        $liveedit.subscribe('/ui/dragdrop/on-sortupdate', renderPlaceholdersForEmptyRegions);
        $liveedit.subscribe('/ui/dragdrop/on-dragover', renderPlaceholdersForEmptyRegions);
    }


    function init() {
        renderPlaceholdersForEmptyRegions();
        attachEventListeners();
        registerSubscribers();
    }


    // Define public API
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