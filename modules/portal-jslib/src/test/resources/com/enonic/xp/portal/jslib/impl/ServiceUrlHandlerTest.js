exports.createUrl = function () {
    var result = execute('portal.serviceUrl', {
        service: 'myservice',
        params: {
            a: 1,
            b: [1, 2]
        }
    });

    assert.assertEquals('/portal/live/stage/some/path/_/service/mymodule/myservice?a=1&b=1&b=2', result);
};
