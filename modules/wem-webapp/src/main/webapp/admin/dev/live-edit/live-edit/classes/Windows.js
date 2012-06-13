AdminLiveEdit.Windows = function()
{
    var util = AdminLiveEdit.Util;

    function init()
    {
        renderPlaceholders();
        initMouseEventListeners();
    }


    function renderPlaceholders()
    {
        getAll().each(function(index) {
            var window = $liveedit(this);
            var windowIsEmpty = util.isElementEmpty(window);
            if ( windowIsEmpty ) {
                appendPlaceholder(window);
            }
        });
    }


    function appendPlaceholder( window )
    {
        var marker = $liveedit( '<div/>', {
            class: 'live-edit-empty-window-placeholder',
            html: 'Empty Window'
        });
        window.append( marker );
    }


    function getAll()
    {
        return $liveedit('[data-live-edit-window]');
    }


    function initMouseEventListeners()
    {
        var highlighter = AdminLiveEdit.Highlighter;

        $liveedit('body').on('hover','[data-live-edit-window]',  function(event) {
            var window = $liveedit(this);
            if (AdminLiveEdit.DragDrop.isDragging() || AdminLiveEdit.PageElementSelector.getSelected()) {
                return false;
            }
            if ( event.type === 'mouseenter' ) {
                highlighter.highlightWindow(window);
            } else {
                highlighter.hide();
            }
        });
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Public

    return {
        init: function() {
            init();
        },

        getAll: function() {
            return getAll();
        },

        renderPlaceholders: function() {
            renderPlaceholders();
        }
    };

}();