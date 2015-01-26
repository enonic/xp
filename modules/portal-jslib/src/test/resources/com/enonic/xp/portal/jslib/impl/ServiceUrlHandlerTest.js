exports.createUrl = function () {
    var result = execute('portal.serviceUrl', {
        service: 'myservice',
        params: {
            a: 1,
            b: [1, 2]
        }
    });

    // NOTE: This is not the actual url. Only a mock representation.
    assert.assertEquals('ServiceUrlParams{params={a=[1], b=[1, 2]}, service=myservice}', result);
};
