StartTest(function(t) {

    function getWindows() {
        return $liveedit('[data-live-edit-type="window"]');
    }

    t.diag('Test namespace');

    t.ok(AdminLiveEdit, 'AdminLiveEdit namespace is here');

    t.diag('Window 5 general test');

    var window5 = getWindows()[4];
    var boxModel = AdminLiveEdit.Util.getBoxModel( window5 );
    t.is( boxModel.width, 130, 'Width should be 300' );
    t.is( boxModel.height, 100, 'Height should be 200' );
    t.is( boxModel.top, 136, 'Offset top should be 136' );
    t.is( boxModel.left, 201, 'Offset left should be 201' );
    t.is( boxModel.paddingBottom, 0, 'Padding bottom should be 0' );
    t.is( boxModel.borderTop, 0, 'Border top should be 0' );

    t.diag('Window 6 padding test');

    var window6 = getWindows()[5];
    boxModel = AdminLiveEdit.Util.getBoxModel( window6 );

    t.is( boxModel.paddingTop, 10, 'Padding bottom should be 10' );
    t.is( boxModel.paddingRight, 10, 'Padding bottom should be 10' );
    t.is( boxModel.paddingBottom, 10, 'Padding bottom should be 10' );
    t.is( boxModel.paddingLeft, 10, 'Padding bottom should be 10' );

    t.diag('Get window content box only');
    boxModel = AdminLiveEdit.Util.getBoxModel( window6, true );
    t.is( boxModel.width, 110, 'Width should be 110' );
    t.is( boxModel.height, 80, 'Height should be 80' );

    t.diag('Window 7 border test');
    var window7 = getWindows()[6];
    boxModel = AdminLiveEdit.Util.getBoxModel( window7 );
    t.is( boxModel.borderTop, 1, 'Border top for window 7 should be 1' );
    t.is( boxModel.borderRight, 1, 'Border right for window 7 should be 1' );
    t.is( boxModel.borderBottom, 1, 'Border bottom for window 7 should be 1' );
    t.is( boxModel.borderLeft, 1, 'Border left for window 7 should be 1' );

});