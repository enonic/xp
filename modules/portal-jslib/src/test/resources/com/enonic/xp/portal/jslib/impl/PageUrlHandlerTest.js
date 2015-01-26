exports.createUrl = function () {
    var result = execute('portal.pageUrl', {
        path: 'a/b',
        params: {
            a: 1,
            b: [1, 2]
        }
    });

    // NOTE: This is not the actual url. Only a mock representation.
    assert.assertEquals('PageUrlParams{params={a=[1], b=[1, 2]}, path=a/b}', result);
};
