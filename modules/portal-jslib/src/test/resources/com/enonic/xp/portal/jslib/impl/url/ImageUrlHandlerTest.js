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

    // NOTE: This is not the actual url. Only a mock representation.
    assert.assertEquals('ImageUrlParams{params={a=[1], b=[1, 2]}, id=123, quality=90, filter=scale(1,1), background=ffffff}', result);
};
