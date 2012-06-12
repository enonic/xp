StartTest(function(t) {
    t.diag('Test that there are no conflicts between jQuery libraries ($liveedit is an alias)');

    t.ok($, '$ is here');
    t.ok($liveedit, '$liveedit is here');

    t.diag('$ version is: ' + $().jquery + ', $liveedit version is: ' + $liveedit().jquery );

    t.ok($.ui, '$.ui is here');
    t.ok($liveedit.ui, '$liveedit.ui is here');

    t.diag('$.ui version is: ' + $.ui.version + ', $liveedit.ui version is: ' + $liveedit.ui.version );
});