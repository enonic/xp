AdminLiveEdit.PageElementSelector = function()
{
    var selected = null;

    function init()
    {
        initMouseClickEvent();
    }


    function selectElement( element )
    {
        var pageOverlay = AdminLiveEdit.PageOverlay;
        var util = AdminLiveEdit.Util;
        var highlighter = AdminLiveEdit.Highlighter;
        var tooltip = AdminLiveEdit.Tooltip;
        var pageElementTypeToSelect = util.getPageElementType( element );
        var elementBoxModel;

        if ( pageElementTypeToSelect === 'window' ) {
            elementBoxModel = util.getBoxModel( element );
            highlighter.highlightWindow( element, true );
        } else if ( pageElementTypeToSelect === 'region' ) {
            elementBoxModel = util.getBoxModel( element, true );
            highlighter.highlightRegion( element, true );
        }

        pageOverlay.clearRectangle( elementBoxModel.left, elementBoxModel.top, elementBoxModel.width, elementBoxModel.height );

        setSelected( element );
        tooltip.moveToPageElement( element );
    }


    function deselect()
    {
        AdminLiveEdit.PageOverlay.hide();
        AdminLiveEdit.Tooltip.hide();
        setSelected(null);
    }


    function setSelected(element)
    {
        selected = element;
    }


    function getSelected()
    {
        return selected;
    }


    function initMouseClickEvent()
    {
        $liveedit('body').on('click', '[data-live-edit-region], [data-live-edit-window]',  function(event) {
            event.stopPropagation();
            selectElement( $liveedit( this ) );
        });
    }


    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Public

    return {
        init: function() {
            init();
        },

        select: function( element ) {
            selectElement( element );
        },

        deselect: function() {
            deselect();
        },

        setSelected: function( element ) {
            setSelected( element );
        },

        getSelected: function() {
            return selected;
        }
    };

}();