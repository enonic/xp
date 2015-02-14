exports.createUrl = function () {
    var result = execute('portal.componentUrl', {
        component: 'mycomp',
        params: {
            a: 1,
            b: [1, 2]
        }
    });

    // NOTE: This is not the actual url. Only a mock representation.
    assert.assertEquals('ComponentUrlParams{params={a=[1], b=[1, 2]}, component=mycomp}', result);
};
