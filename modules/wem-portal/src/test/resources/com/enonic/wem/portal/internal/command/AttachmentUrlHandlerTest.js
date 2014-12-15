exports.createUrl = function () {
    var result = execute('portal.attachmentUrl', {
        params: {
            a: 1,
            b: [1, 2]
        }
    });

    assert.assertEquals('/root/portal/live/stage/some/path?a=1&b=1&b=2', result);
};
