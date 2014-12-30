exports.test = function () {
    var relative = resolve('./test.html').toString();
    assert.assertEquals('mymodule:/resolve/other/test.html', relative);

    var absolute = resolve('/test.html').toString();
    assert.assertEquals('mymodule:/test.html', absolute);
};
