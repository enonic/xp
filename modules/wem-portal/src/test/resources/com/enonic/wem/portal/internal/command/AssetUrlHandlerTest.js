exports.createUrl = function () {
    var result = execute('portal.assetUrl', {
        path: 'styles/my.css',
        params: {
            a: 1,
            b: [1, 2]
        }
    });

    assert.assertEquals('/root/portal/live/stage/some/path/_/public/mymodule/styles/my.css?a=1&b=1&b=2', result);
};
