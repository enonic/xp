exports.test = function () {
    var relative = resolve('./test.html').toString();
    assert.assertEquals('myapplication:/resolve/other/test.html', relative);

    var absolute = resolve('/test.html').toString();
    assert.assertEquals('myapplication:/test.html', absolute);
};
