AdminLiveEdit.Highlighter = function()
{

    function init()
    {
        createHighlighter();
    }


    function createHighlighter()
    {
        var marker = $liveedit( '<div/>', {
            id: 'live-edit-highlighter'
        });
        $liveedit( 'body' ).append( marker );
    }


    function highlightWindow( windowElement, select )
    {
        var util = AdminLiveEdit.Util;
        var highlighterDiv = $liveedit( '#live-edit-highlighter' );
        var windowBoxModel = util.getBoxModelSize( windowElement );

        windowElement.addClass( 'live-edit-position-relative' );

        highlighterDiv.attr( 'class', '' );
        if ( select ) {
            highlighterDiv.addClass( 'live-edit-window-selected' );
        } else {
            highlighterDiv.addClass( 'live-edit-window-highlighter' );
        }

        var elementBorder = parseInt( windowElement.css( 'borderTopWidth' ) );
        var markerOffset = (elementBorder > 0) ? parseInt( '-' + (elementBorder * 2) ) : -1;

        var w = windowBoxModel.width - 4 + elementBorder;
        var h = windowBoxModel.height - 4 + elementBorder;
        if ( elementBorder === 0 ) {
            w += 1;
            h += 1;
        }

        highlighterDiv.css( {
            top: markerOffset + 'px',
            left: markerOffset + 'px',
            width: w,
            height: h
        });

        windowElement.append( highlighterDiv );
    }


    function highlightRegion( regionElement, select )
    {
        var util = AdminLiveEdit.Util;
        var marker = $liveedit( '#live-edit-highlighter' );
        var regionBoxModel = util.getBoxModelSize( regionElement, true );

        regionElement.addClass( 'live-edit-position-relative' );

        marker.attr( 'class', '' );

        if ( select ) {
            marker.addClass( 'live-edit-region-selected' );
        } else {
            marker.addClass( 'live-edit-region-highlighter' );
        }

        marker.css( {
            top: regionBoxModel.paddingTop,
            left: regionBoxModel.paddingLeft,
            width: regionBoxModel.width - 4, // minus marker css border size
            height: regionBoxModel.height - 4 // minus marker css border size
        });

        regionElement.append( marker );
    }


    function hide()
    {
        var marker = $liveedit( '#live-edit-highlighter' );
        marker.css( {
            top: '-5000px',
            left: '-5000px'
        });
        $liveedit( 'body' ).append( marker );
        marker.parent().removeClass( 'live-edit-position-relative' );
    }


    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Public

    return {
        init: init,
        highlightWindow: function(window, select) {
            highlightWindow( window, select )
        },
        highlightRegion: function(region, select) {
            highlightRegion( region, select )
        },
        hide: hide
    };

}();