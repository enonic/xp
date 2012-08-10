StartTest(function (t) {
    t.diag('Test that there are no conflicts between jQuery libraries ($liveedit is an alias)');

    t.ok($, 'jQuery\'s global object $ is here');
    t.ok($liveedit, 'Live Edit\'s global object $liveedit is here');

    t.diag('$ version is: ' + $().jquery + ', $liveedit version is: ' + $liveedit().jquery);

    t.ok($.ui, 'Global object $.ui is here');
    t.ok($liveedit.ui, 'Global object $liveedit.ui is here');

    t.diag('$.ui version is: ' + $.ui.version + ', $liveedit.ui version is: ' + $liveedit.ui.version);
});