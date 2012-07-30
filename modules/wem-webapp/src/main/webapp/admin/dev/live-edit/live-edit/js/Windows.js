AdminLiveEdit.Windows = (function () {

    var SELECTOR = '[data-live-edit-type=window]';


    function getAll() {
        return $liveedit(SELECTOR);
    }


    function appendEmptyEmptyWindowPlaceholder($window) {
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
                appendEmptyEmptyWindowPlaceholder($window);
            }
        });
    }


    function init() {
        renderPlaceholdersForEmptyWindows();
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