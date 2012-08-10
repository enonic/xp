StartTest(function (t) {
    t.diag('Test that there are no conflicts between $liveedit and Prototype ($liveedit is an alias)');

    t.ok($, 'Prototype\'s global object $ is here');
    t.ok($liveedit, 'Live Edit\'s global object $liveedit is here');

    t.diag('Prototype version is: ' + Prototype.Version + ', $liveedit version is: ' + $liveedit().jquery);

    t.ok($liveedit.ui, 'Global object $liveedit.ui is here');

});