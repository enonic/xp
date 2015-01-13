exports.createUrl = function () {
    var result = execute('portal.assetUrl', {
        path: 'styles/my.css',
        params: {
            a: 1,
            b: [1, 2]
        }
    });

    assert.assertEquals('/portal/stage/some/path/_/asset/mymodule/styles/my.css?a=1&b=1&b=2', result);
};
