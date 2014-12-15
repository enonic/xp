exports.createUrl = function () {
    var result = execute('portal.componentUrl', {
        component: 'mycomp',
        params: {
            a: 1,
            b: [1, 2]
        }
    });

    assert.assertEquals('/root/portal/live/stage/some/path/_/component/mycomp?a=1&b=1&b=2', result);
};
