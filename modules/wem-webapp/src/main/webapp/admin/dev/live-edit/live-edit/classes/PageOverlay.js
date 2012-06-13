AdminLiveEdit.PageOverlay = function()
{
    var util = AdminLiveEdit.Util;
    var defaultRGBColor = [0, 0, 0, 0.4];

    function init()
    {
        createOverlays();
        initOverlayClickEvent();
    }


    function createOverlays()
    {
        $liveedit( 'body' ).append( '<div class="live-edit-overlay" id="live-edit-overlay-north"/>' );
        $liveedit( 'body' ).append( '<div class="live-edit-overlay" id="live-edit-overlay-east"/>' );
        $liveedit( 'body' ).append( '<div class="live-edit-overlay" id="live-edit-overlay-south"/>' );
        $liveedit( 'body' ).append( '<div class="live-edit-overlay" id="live-edit-overlay-west"/>' );
    }


    function clearRectangle( x, y, w, h )
    {
        var documentSize = AdminLiveEdit.Util.getDocumentSize();
        var docWidth = documentSize.width;
        var docHeight = documentSize.height;

        var north = $liveedit('#live-edit-overlay-north');
        var east = $liveedit('#live-edit-overlay-east');
        var south = $liveedit('#live-edit-overlay-south');
        var west = $liveedit('#live-edit-overlay-west');

        north.css({
            top: 0,
            left: 0,
            width: docWidth,
            height: y
        });

        east.css({
            top: y,
            left: x + w,
            width: docWidth - (x + w),
            height: h
        });

        south.css({
            top: y + h,
            left: 0,
            width: docWidth,
            height: docHeight - ( y + h )
        });

        west.css({
            top: y,
            left: 0,
            width: 0 + x,
            height: h
        });
    }


    function hideOverlay()
    {
        $liveedit( '.live-edit-overlay' ).css({
            top: '-15000px',
            left: '-15000px'
        });
    }


    function initOverlayClickEvent()
    {
        var util = AdminLiveEdit.Util;
        var pageElementSelector = AdminLiveEdit.PageElementSelector;

        $liveedit( '.live-edit-overlay' ).click( function( event ) {
            hideOverlay();
            AdminLiveEdit.Highlighter.hide();
            AdminLiveEdit.Tooltip.hide();

            var closestElementFromPoint = util.getClosestPageElementFromPoint( event.originalEvent.clientX,
                    event.originalEvent.clientY );
            if ( closestElementFromPoint.length > 0 ) {
                pageElementSelector.select( closestElementFromPoint );
            } else {
                pageElementSelector.setSelected( null );
            }
        });
    }


    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Public

    return {
        init: function() {
            init();
        },

        clearRectangle: function(x, y, w, h) {
            clearRectangle(x, y, w, h);
        },

        hide: function() {
            hideOverlay();
        }
    };

}();