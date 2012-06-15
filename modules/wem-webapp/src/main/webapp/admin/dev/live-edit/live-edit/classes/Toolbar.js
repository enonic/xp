AdminLiveEdit.Toolbar = function()
{
    function init()
    {
        createToolbar();
    }


    function createToolbar()
    {
        var button = AdminLiveEdit.Button;
        var toolbar = $liveedit( '<div id="live-edit-toolbar"><div id="live-edit-toolbar-inner"></div></div>' );

        var parentButton = button.create({
            text: 'Parent',
            iconCls: 'live-edit-icon-parent',
            handler: function() {
                var elementSelector = AdminLiveEdit.ElementSelector;
                var selected = elementSelector.getSelected();
                var parentOfSelected = AdminLiveEdit.Util.getParentPageElement( selected );
                if ( parentOfSelected.length > 0 ) {
                    elementSelector.select( parentOfSelected );
                }
            }
        });

        $liveedit( 'body' ).append( toolbar );
        $liveedit( '#live-edit-toolbar-inner' ).append( parentButton );
    }


    function moveTo( element )
    {
        var toolbar = getToolbar();
        var util = AdminLiveEdit.Util;

        var elementType = util.getPageElementType(element);

        var elementBoxModel = util.getBoxModel( element, elementType === 'region' );

        var top = elementBoxModel.top;

        var left = (elementBoxModel.left + elementBoxModel.width) - toolbar.outerWidth();

        toolbar.css({
            top: top,
            left: left
        });
    }


    function getToolbar()
    {
        return $liveedit( '#live-edit-toolbar' );
    }


    function hide()
    {
        getToolbar().css({
            top: '-5000px',
            left: '-5000px'
        });
    }


    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Public

    return {
        init: function() {
            init();
        },

        moveTo: function( element ) {
            moveTo( element );
        },

        hide: function() {
            hide();
        }
    };

}();