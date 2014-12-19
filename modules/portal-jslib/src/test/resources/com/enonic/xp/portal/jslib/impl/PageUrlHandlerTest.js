exports.createUrl = function () {
    var result = execute('portal.pageUrl', {
        path: 'a/b',
        params: {
            a: 1,
            b: [1, 2]
        }
    });

    assert.assertEquals('/root/portal/live/stage/a/b?a=1&b=1&b=2', result);
};
