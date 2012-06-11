AdminLiveEdit.Regions = function()
{
    var util = AdminLiveEdit.Util;

    function init()
    {
        renderPlaceholders();
        initMouseEventListeners();
    }


    function initMouseEventListeners()
    {
        $liveedit('body').on('hover', '[data-live-edit-region]', function(event) {
            var region = $liveedit(this);
            var placeholder = region.children('.live-edit-empty-region-placeholder');
            if ( placeholder.length > 0 ) {
                if ( event.type === 'mouseenter' ) {
                    placeholder.addClass('live-edit-empty-region-placeholder-hover');
                } else {
                    placeholder.removeClass('live-edit-empty-region-placeholder-hover');
                }
            }
        });
    }


    function renderPlaceholders()
    {
        removeAllPlaceholders();

        var regions = getAll();
        regions.each(function(index) {
            var region = $liveedit(this);
            var regionIsEmpty = isRegionEmpty(region);
            if ( regionIsEmpty ) {
                appendPlaceholderToRegion( region );
            }
        });
    }


    function appendPlaceholderToRegion( region )
    {
        var placeholder = $liveedit( '<div/>', {
            class: 'live-edit-empty-region-placeholder',
            html: 'Drag components here'
        } );
        region.append( placeholder );
    }


    function getAll()
    {
        return $liveedit( '[data-live-edit-region]' );
    }


    function removeAllPlaceholders()
    {
        $liveedit( '.live-edit-empty-region-placeholder' ).remove();
    }


    function isRegionEmpty(region)
    {
        var children = region.children('[data-live-edit-window]:not(:hidden)');
        var dropTargetPlaceHolder = region.children('.live-edit-dd-drop-target-placeholder');
        // Region har kun dtarget placeholder
        // region.children().length === 1 && dropTargetPlaceHolder > 0;
        return children.length === 0 && dropTargetPlaceHolder.length === 0;
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