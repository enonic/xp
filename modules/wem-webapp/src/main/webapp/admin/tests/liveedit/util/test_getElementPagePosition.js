StartTest(function(t) {

    function getWindow() {
        return $liveedit('[data-live-edit-window]')[0];
    }

    t.diag('Change window position to top:100px, left:243px');

    $liveedit( getWindow() ).css( 'top', '100px' );
    $liveedit( getWindow() ).css( 'left', '243px' );

    var windowPosition = AdminLiveEdit.Util.getElementPagePosition( getWindow() );
    t.is( windowPosition.top, 100, 'Window should be positioned top at 100px' );
    t.is( windowPosition.left, 243, 'Window should be positioned left at 243px' );

});