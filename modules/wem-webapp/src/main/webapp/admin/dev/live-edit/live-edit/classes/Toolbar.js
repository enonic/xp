AdminLiveEdit.Toolbar = function()
{
    function init()
    {
        buildToolbar();
    }


    function buildToolbar()
    {
        var toolbar = $liveedit('<div/>', {
            id: 'live-edit-toolbar',
            html: 'Parent'
        });

        toolbar.click(function() {
            AdminLiveEdit.Highlighter.selectParent();
        });

        $liveedit( 'body' ).append( toolbar );
    }


    function moveToHighlighter( highlighter )
    {
        var util = AdminLiveEdit.Util;
        var toolbar = getToolbar();
        var highlighterBoxModel = util.getBoxModelSize(highlighter);

        toolbar.css({
            top: highlighterBoxModel.top + 2,
            left: (highlighterBoxModel.left + highlighterBoxModel.width) - toolbar.outerWidth() -2
        });
    }


    function show()
    {
        getToolbar().show();
    }


    function hide()
    {
        getToolbar().hide();
    }


    function getToolbar()
    {
        return $liveedit( '#live-edit-toolbar' );
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Public

    return {
        init: function() {
            init();
        },

        show: function() {
            show();
        },

        hide: function() {
            hide();
        },

        moveToHighlighter: function(highlighter) {
            moveToHighlighter(highlighter);
        }
    };

}();