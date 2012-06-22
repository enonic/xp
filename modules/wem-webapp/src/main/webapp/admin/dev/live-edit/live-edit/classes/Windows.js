AdminLiveEdit.Windows = (function () {
    var util = AdminLiveEdit.Util;


    function getAll() {
        return $liveedit('[data-live-edit-type=window]');
    }


    function appendEmptyWindowPlaceholder(window) {
        var marker = $liveedit('<div/>', {
            'class': 'live-edit-empty-window-placeholder',
            'html': 'Empty Window'
        });
        window.append(marker);
    }


    function renderPlaceholdersForEmptyWindows() {
        getAll().each(function (index) {
            var window = $liveedit(this);
            var windowIsEmpty = util.isElementEmpty(window);
            if (windowIsEmpty) {
                appendEmptyWindowPlaceholder(window);
            }
        });
    }


    function initMouseHoverEventListeners() {
        var highlighter = AdminLiveEdit.Highlighter;
        $liveedit('body').on('hover', '[data-live-edit-type=window]', function (event) {
            var window = $liveedit(this);
            if (AdminLiveEdit.DragDrop.isDragging() || AdminLiveEdit.ElementSelector.getSelected()) {
                return false;
            }
            if (event.type === 'mouseenter') {
                highlighter.highlightWindow(window);
            } else {
                highlighter.hide();
            }
        });
    }


    function init() {
        renderPlaceholdersForEmptyWindows();

        // Hover events are not necessary for touch.
        if (!AdminLiveEdit.Util.supportsTouch()) {
            initMouseHoverEventListeners();
        }
    }


    // *****************************************************************************************************************
    // Public
    return {
        init: function () {
            init();
        },

        getAll: function () {
            return getAll();
        },

        renderPlaceholdersForEmptyWindows: function () {
            renderPlaceholdersForEmptyWindows();
        }
    };

}());