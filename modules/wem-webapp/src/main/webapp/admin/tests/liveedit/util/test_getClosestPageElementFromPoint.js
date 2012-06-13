StartTest(function(t) {

    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    // Set up

    // Remove the hardcoded window
    $liveedit( '[data-live-edit-window]' ).remove();

    t.diag('Add some regions with windows');

    function addWindowsToRegion(region, regionIndex)
    {
        for (var i = 0; i < 3; i++ ) {
            var window = $liveedit('<div ></div>');
            window.css({
                backgroundColor: 'lightsteelblue',
                width: '130px',
                height: '100px',
                marginTop: '10px'
            });

            window.attr('id', 'window-' + regionIndex + '-' + i).attr( 'data-live-edit-window', 'window-' + regionIndex + '-' + i ).html('Window ' + (i+1));
            region.append(window);
        }
    }

    function createRegions()
    {
        for (var i = 0; i < 3; i++ ) {
            var region = $liveedit('<div class="region"></div>');
            region.css({
                width: '180px',
                height: '400px'
            });
            region.attr('id', 'region-' + i).attr( 'data-live-edit-region', 'region' + i ).css( 'margin-left', '10px' ).html( 'Region ' + (i + 1) );

            addWindowsToRegion(region, i);

            $liveedit( 'body' ).append( region );
        }
    }

    createRegions();

    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    // Tests

    var element = AdminLiveEdit.Util.getClosestPageElementFromPoint(260, 173);
    t.is( element.attr('id'), 'window-1-1', 'Clicked element should be window 2 in region 2' );

    element = AdminLiveEdit.Util.getClosestPageElementFromPoint(510, 269);
    t.is( element.attr('id'), 'window-2-2', 'Clicked element should be window 3 in region 3' );

    element = AdminLiveEdit.Util.getClosestPageElementFromPoint(353, 267);

    t.is( element.attr('id'), 'region-1', 'Clicked region should be region 2' );

});