var t = require('/lib/xp/testing');

t.mock('/lib/xp/something', {
    a: 1,
    b: 2
});

exports.testMock = function () {
    var mock = require('/lib/xp/something');
    t.assertEquals(1, mock.a);
    t.assertEquals(2, mock.b);
};
