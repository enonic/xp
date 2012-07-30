AdminLiveEdit.Regions = function () {

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
        var hasNotWindows = region.children(AdminLiveEdit.Windows.SELECTOR + ':not(:hidden)').length === 0;
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


    function registerSubscribers() {
        $liveedit.subscribe('/page/component/sortupdate', renderPlaceholdersForEmptyRegions);
        $liveedit.subscribe('/page/component/sortupdate', renderPlaceholdersForEmptyRegions);
        $liveedit.subscribe('/page/component/dragover', renderPlaceholdersForEmptyRegions);
    }


    function init() {
        renderPlaceholdersForEmptyRegions();
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

}();