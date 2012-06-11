AdminLiveEdit.PageCanvas = function()
{
    var util = AdminLiveEdit.Util;

    var defaultCanvasRGBColor = [0, 0, 0, 0.4];
    var currentCanvasRGBColor = defaultCanvasRGBColor;
    var currentRectangleSize = [];

    function init()
    {
        createCanvasElement();
        initCanvasClickEvent();
        initResizeWindowEvent();
    }


    function createCanvasElement()
    {
        var canvas = $liveedit( '<canvas id="live-edit-canvas"/>' );
        var context = canvas[0];
        $liveedit( 'body' ).append( canvas );

        updateCanvas();
        fillCanvas( defaultCanvasRGBColor );
    }


    function clearRectangle( x, y, w, h )
    {
        currentRectangleSize = [x, y, w, h];

        var context = getCanvasElement().getContext( '2d' );
        context.clearRect( x, y, w, h );
    }


    function fillCanvas( rgbaArray )
    {
        currentCanvasRGBColor = rgbaArray;

        var context = getCanvasElement().getContext( '2d' );
        var documentSize = util.getDocumentSize();

        context.clearRect(0, 0, documentSize.width, documentSize.height);
        context.fillStyle = 'rgba(' + rgbaArray + ')';
        context.fillRect( 0, 0, documentSize.width, documentSize.height );
    }


    function updateCanvas()
    {
        resizeCanvas();
        fillCanvas(currentCanvasRGBColor);
    }


    function resizeCanvas()
    {
        var canvas = getCanvasElement();
        var documentSize = util.getDocumentSize();
        canvas.width = documentSize.width;
        canvas.height = documentSize.height;
    }


    function showCanvasElement()
    {
        $liveedit( getCanvasElement() ).show();
    }


    function hideCanvasElement()
    {
        $liveedit( getCanvasElement() ).hide();
    }


    function getCanvasElement()
    {
        return $liveedit('#live-edit-canvas')[0];
    }


    function initCanvasClickEvent()
    {
        var util = AdminLiveEdit.Util;
        var pageElementSelector = AdminLiveEdit.PageElementSelector;

        $liveedit( getCanvasElement() ).click( function( event ) {
            hideCanvasElement();
            AdminLiveEdit.Highlighter.hide();
            AdminLiveEdit.Tooltip.hide();

            var closestElementFromPoint = util.getClosestPageElementFromPoint( event.originalEvent.clientX,
                    event.originalEvent.clientY );
            if ( closestElementFromPoint.length > 0 ) {
                pageElementSelector.selectElement( closestElementFromPoint );
            } else {
                pageElementSelector.setSelected( null );
            }
        });
    }


    // TODO: Move to global?
    function initResizeWindowEvent()
    {
        var resizeTimeout = -1;
        $liveedit(window).resize(function() {
            hideCanvasElement();
            AdminLiveEdit.Highlighter.hide();
            AdminLiveEdit.PageElementSelector.setSelected( null );

            if ( resizeTimeout !== false ) {
                clearTimeout( resizeTimeout );
            }

            resizeTimeout = setTimeout( function(){
            }, 200 );
        });
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Public

    return {
        init: function() {
            init();
        },

        fillCanvas: function(rgba) {
            fillCanvas(rgba);
        },

        updateCanvas: function() {
            updateCanvas();
        },

        resizeCanvas: function() {
            resizeCanvas();
        },

        clearRectangle: function(x, y, w, h) {
            clearRectangle(x, y, w, h);
        },

        getCanvasElement: function() {
            return getCanvasElement();
        },

        show: function() {
            showCanvasElement();
        },

        hide: function() {
            hideCanvasElement();
        }
    };

}();