AdminLiveEdit.Util = function()
{
    return {
        getDocumentSize: function()
        {
            var doc = $liveedit( document );
            return {
                width: doc.width(),
                height: doc.height()
            };
        },


        getViewPortSize: function()
        {
            var win = $liveedit( window );
            return {
                width: win.width(),
                height: win.height()
            };
        },


        getBoxModel: function( element, contentOnly )
        {
            var el = $liveedit( element );
            var offset = el.offset(),
                    top = offset.top,
                    left = offset.left,
                    width = el.outerWidth(),
                    height = el.outerHeight();

            var mt = parseInt( el.css( 'marginTop' ) );
            var mr = parseInt( el.css( 'marginRight' ) );
            var mb = parseInt( el.css( 'marginBottom' ) );
            var ml = parseInt( el.css( 'marginLeft' ) );

            var bt = parseInt( el.css( 'borderTopWidth' ) );
            var br = parseInt( el.css( 'borderRightWidth' ) );
            var bb = parseInt( el.css( 'borderBottomWidth' ) );
            var bl = parseInt( el.css( 'borderLeftWidth' ) );

            var pt = parseInt( el.css( 'paddingTop' ) );
            var pr = parseInt( el.css( 'paddingRight' ) );
            var pb = parseInt( el.css( 'paddingBottom' ) );
            var pl = parseInt( el.css( 'paddingLeft' ) );

            // TODO calculate margin and border
            if ( contentOnly ) {
                top = top + pt;
                left = left + pl;
                width = width - (pl + pr);
                height = height - (pt + pb);
            }

            return {
                top: top,
                left: left,
                width: width,
                height: height,
                borderTop: bt,
                borderRight: br,
                borderBottom: bb,
                borderLeft: bl,
                paddingTop: pt,
                paddingRight: pr,
                paddingBottom: pb,
                paddingLeft: pl
            }
        },


        getElementPagePosition: function(element)
        {
            return $liveedit( element ).position();
        },


        getClosestPageElementFromPoint: function( x, y )
        {
            var element = [];
            var elementFromPoint = $liveedit( this.elementFromPoint( x, y ) );

            var parent = elementFromPoint.parents( '[data-live-edit-type=window]' );
            var isWindowOrRegion = elementFromPoint.is( '[data-live-edit-type=window]' ) || elementFromPoint.is( '[data-live-edit-type=region]');
            if ( isWindowOrRegion ) {
                element = elementFromPoint;
            } else if ( parent.length > 0 ) {
                element = parent;
            } else {
                element = elementFromPoint.parents( '[data-live-edit-type=region]' );
            }
            return element;
        },


        getParentPageElement: function( element )
        {
            // Right now region is the only parent :)
            return element.parents('[data-live-edit-type=region]');
        },


        elementFromPoint: function( x, y )
        {
            return document.elementFromPoint( x, y );
        },


        getPageElementType: function( element )
        {
            return element.data()['liveEditType'];
        },


        getPageElementName: function( element )
        {
            return element.data()['liveEditName'];
        },


        isElementEmpty: function( element )
        {
            return $liveedit( element ).children().length === 0;
        },


        supportsTouch: function()
        {
            return 'ontouchend' in document;
        }

    };

}();

