StartTest(function(t) {
    t.diag('Test that there are no conflicts between jQuery libraries ($liveedit is an alias)');

    t.ok($, 'jQuery\'s global variable $ is here');
    t.ok($liveedit, 'Live Edit\'s global $liveedit is here');

    t.diag('$ version is: ' + $().jquery + ', $liveedit version is: ' + $liveedit().jquery );

    t.ok($.ui, 'Global variable $.ui is here');
    t.ok($liveedit.ui, 'Global variable $liveedit.ui is here');

    t.diag('$.ui version is: ' + $.ui.version + ', $liveedit.ui version is: ' + $liveedit.ui.version );
});