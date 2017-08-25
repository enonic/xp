var t = require('/lib/xp/testing');

var num = 10;

exports.before = function () {
    num += 10;
};

exports.after = function () {
    num = 10;
};

exports.testLifecycle1 = function () {
    t.assertEquals(20, num);
};

exports.testLifecycle2 = function () {
    t.assertEquals(20, num);
};

