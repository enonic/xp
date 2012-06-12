AdminLiveEdit.Tooltip = function()
{
    function init()
    {
        createTooltip();
    }


    function moveToPageElement( element )
    {
        var util = AdminLiveEdit.Util;
        var elementType = util.getPageElementType( element );
        setText( elementType, ' - ' + (element.data()['liveEditWindow'] || element.data()['liveEditRegion']) );

        var tooltip = getTooltip();
        var elementBoxModel = AdminLiveEdit.Util.getBoxModelSize( element, (elementType === 'region') );
        var top = elementBoxModel.top - 50;
        var left = elementBoxModel.left + (elementBoxModel.width/2) - (tooltip.outerWidth()/2);

        tooltip.css( {
            top: top,
            left: left
        });

        scrollTooltipIntoView();
    }


    function setText(typeText, nameText)
    {
        $liveedit( '#live-edit-tooltip-type-text' ).html(typeText);
        $liveedit( '#live-edit-tooltip-name-text' ).html(nameText);
    }


    function hide()
    {
        $liveedit( '#live-edit-tooltip' ).css({
            top: '-5000px',
            left: '-5000px'
        })
    }


    function scrollTooltipIntoView()
    {
        var util = AdminLiveEdit.Util;
        var elementTopPosition = util.getElementPagePosition( getTooltip() ).top;
        if ( elementTopPosition <= window.pageYOffset ) {
            $liveedit( 'html, body' ).animate( {scrollTop: elementTopPosition-10}, 200 );

        }
    }


    function createTooltip()
    {
        var tooltip = $liveedit('<div id="live-edit-tooltip"><span id="live-edit-tooltip-type-text"><!-- --></span><span id="live-edit-tooltip-name-text"><!-- --></span><div id="live-edit-tooltip-arrow-border"></div><div id="live-edit-tooltip-arrow"></div></div>');
        $liveedit('body' ).append(tooltip);
    }


    function getTooltip()
    {
        return $liveedit('#live-edit-tooltip');
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Public

    return {
        init: function() {
            init();
        },
        moveToPageElement: function( element ) {
            moveToPageElement( element );
        },
        hide: function() {
            hide( );
        }
    };

}();