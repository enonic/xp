var assert = Java.type('org.junit.Assert');

exports.test = function () {
    var relative = resolve('./test.html').toString();
    assert.assertEquals('myapplication:/resolve/other/test.html', relative);

    var absolute = resolve('/test.html').toString();
    assert.assertEquals('myapplication:/site/test.html', absolute);
};
