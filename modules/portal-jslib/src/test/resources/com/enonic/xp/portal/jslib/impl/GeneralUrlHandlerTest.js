exports.createUrl = function () {
    var result = execute('portal.url', {
        path: 'a/b',
        params: {
            a: 1,
            b: [1, 2]
        }
    });

    assert.assertEquals('/root/portal/a/b?a=1&b=1&b=2', result);
};
