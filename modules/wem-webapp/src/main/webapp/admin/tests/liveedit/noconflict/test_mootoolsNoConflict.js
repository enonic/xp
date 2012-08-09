StartTest(function (t) {
    t.diag('Test that there are no conflicts between $liveedit and MooTools ($liveedit is an alias)');

    t.ok($, 'MooTool\'s global variable $ is here');
    t.ok($liveedit, 'Live Edit\'s global $liveedit is here');

    t.diag('MooTool version is: ' + MooTools.version + ', $liveedit version is: ' + $liveedit().jquery);

    t.ok($liveedit.ui, 'Global variable $liveedit.ui is here');

});