AdminLiveEdit.Toolbar = function()
{
    var f = {
        region: ['parent'],
        window: ['parent', 'edit', 'settings', 'remove']
    };


    function init()
    {
        createToolbar();
        createToolbarButtons();
    }


    function createToolbar()
    {
        $liveedit( 'body' ).append( '<div id="live-edit-toolbar"><div id="live-edit-toolbar-inner"></div></div>' );
    }


    function createToolbarButtons()
    {
        var button = AdminLiveEdit.Button;

        // Hard code the buttons for now.
        var selectParentButton = button.create({
            text: 'Parent',
            id: 'live-edit-button-parent',
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

        /*
        var editButton = button.create({
            text: 'Edit',
            id: 'live-edit-button-edit',
            iconCls: 'live-edit-icon-edit'
        });
        */

        var settingsButton = button.create({
            text: 'Settings',
            id: 'live-edit-button-settings',
            iconCls: 'live-edit-icon-settings'
        });

        var removeButton = button.create({
            text: 'Remove',
            id: 'live-edit-button-remove',
            iconCls: 'live-edit-icon-remove'
        });

        var container = $liveedit( '#live-edit-toolbar-inner' );
        container.append( selectParentButton );
        // container.append( editButton );
        container.append( settingsButton );
        container.append( removeButton );
    }


    function moveTo( element )
    {
        var util = AdminLiveEdit.Util;
        var toolbar = getToolbar();
        var pageElementType = util.getPageElementType( element );

        // TODO: Move this
        // Show/hide buttons based on page element type
        toolbar.find( '.live-edit-button' ).each( function( i ) {
            var button = $( this );
            if ( pageElementType === 'region' && i > 0 ) {
                button.hide();
            } else {
                button.show();
            }
        });

        var elementBoxModel = util.getBoxModel( element, pageElementType === 'region' );
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