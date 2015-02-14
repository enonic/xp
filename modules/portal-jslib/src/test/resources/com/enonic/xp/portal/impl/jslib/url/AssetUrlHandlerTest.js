exports.createUrl = function () {
    var result = execute('portal.assetUrl', {
        path: 'styles/my.css',
        params: {
            a: 1,
            b: [1, 2]
        }
    });

    // NOTE: This is not the actual url. Only a mock representation.
    assert.assertEquals('AssetUrlParams{params={a=[1], b=[1, 2]}, path=styles/my.css}', result);
};
