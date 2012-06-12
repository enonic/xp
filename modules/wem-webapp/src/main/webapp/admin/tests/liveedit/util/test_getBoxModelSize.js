StartTest(function(t) {

    function getWindow() {
        return $liveedit('[data-live-edit-window]')[0];
    }

    t.ok(AdminLiveEdit, 'AdminLiveEdit namespace is here');

    var boxModel = AdminLiveEdit.Util.getBoxModelSize( getWindow() );
    t.is( boxModel.width, 300, 'Window should have width 300px' );
    t.is( boxModel.height, 200, 'Window should have height 200px' );
    t.is( boxModel.top, 30, 'Window should have position top 30px' );
    t.is( boxModel.left, 133, 'Window should have position left 133px' );
    t.is( boxModel.paddingBottom, 0, 'Padding bottom should be 0' );
    t.is( boxModel.borderTop, 1, 'Border top should be 1px' );


    t.diag('Change window padding from 0 to 10 pixels');

    $liveedit( getWindow() ).css( 'padding', '10px' );
    boxModel = AdminLiveEdit.Util.getBoxModelSize( getWindow() );
    t.is( boxModel.paddingTop, 10, 'Padding bottom should be 10px' );
    t.is( boxModel.paddingRight, 10, 'Padding right should be 10px' );
    t.is( boxModel.paddingBottom, 10, 'Padding bottom should be 10px' );
    t.is( boxModel.paddingLeft, 10, 'Padding left should be 10px' );

    t.diag('Get window content box only');

    boxModel = AdminLiveEdit.Util.getBoxModelSize( getWindow(), true );
    t.is( boxModel.width, 280, 'Window should have width 280' );
    t.is( boxModel.height, 180, 'Window should have height 180' );

    t.diag('Remove border');

    $liveedit( getWindow() ).css( 'border', 'none' );
    boxModel = AdminLiveEdit.Util.getBoxModelSize( getWindow() );
    t.is( boxModel.borderTop, 0, 'Border top should be 0' );
    t.is( boxModel.borderRight, 0, 'Border right should be 0' );
    t.is( boxModel.borderBottom, 0, 'Border bottom should be 0' );
    t.is( boxModel.borderLeft, 0, 'Border left should be 0' );

    t.diag('Restore border');
    $liveedit( getWindow() ).css( 'border', '1px solid black' );

});