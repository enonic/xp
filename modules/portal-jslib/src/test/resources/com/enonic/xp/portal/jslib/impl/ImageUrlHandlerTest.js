exports.createUrl = function () {
    var result = execute('portal.imageUrl', {
        id: '123',
        background: 'ffffff',
        quality: 90,
        filter: 'scale(1,1)',
        params: {
            a: 1,
            b: [1, 2]
        }
    });

    assert.assertEquals('/root/portal/live/stage/some/path/_/image/id/123?filter=scale%281%2C1%29&a=1&b=1&b=2&background=ffffff&quality=90',
        result);
};
