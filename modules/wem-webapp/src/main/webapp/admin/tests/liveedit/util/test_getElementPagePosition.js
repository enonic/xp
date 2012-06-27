StartTest(function (t) {

    var window4 = $liveedit('[data-live-edit-type="window"]')[3];
    var pagePosition = AdminLiveEdit.Util.getElementPagePosition(window4);

    t.diag('Test window 4 page position');
    t.is(pagePosition.top, 16, 'Page position top should 16');
    t.is(pagePosition.left, 201, 'Page position left should 201');

    t.diag('Test window 5 page position');
    var window5 = $liveedit('[data-live-edit-type="window"]')[4];
    pagePosition = AdminLiveEdit.Util.getElementPagePosition(window5);

    t.is(pagePosition.top, 126, 'Page position top should 126');
    t.is(pagePosition.left, 201, 'Page position left should 201');

});