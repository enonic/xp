StartTest(function (t) {
    t.diag('Test that there are no conflicts between $liveedit and MooTools ($liveedit is an alias)');

    t.ok($, 'MooTool\'s global object $ is here');
    t.ok($liveedit, 'Live Edit\'s global object $liveedit is here');

    t.diag('MooTool version is: ' + MooTools.version + ', $liveedit version is: ' + $liveedit().jquery);

    t.ok($liveedit.ui, 'Global object $liveedit.ui is here');

});