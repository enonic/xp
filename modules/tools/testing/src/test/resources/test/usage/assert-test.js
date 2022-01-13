/* global Java*/
var t = require('/lib/xp/testing');

exports.testAssert = function () {
    t.assertFalse(false);
    t.assertFalse(false);
    t.assertTrue(true);
    t.assertEquals(1, 1);
    t.assertNotEquals(1, 2);
    t.assertEquals('1', '1');
    t.assertNotEquals('1', '2');
    t.assertJson({}, {});
    t.assertJsonEquals({}, {});
    t.assertThrows(() => t.assertJsonEquals({a: 1}, {}));
    t.assertNull(undefined, 'message');
    t.assertNotNull(1, 'message');
};

exports.testAssertWithMessage = function () {
    t.assertFalse(false, 'message');
    t.assertTrue(true, 'message');
    t.assertEquals(1, 1, 'message');
    t.assertNotEquals(1, 2, 'message');
    t.assertEquals('1', '1', 'message');
    t.assertNotEquals('1', '2', 'message');
    t.assertJson({}, {}, 'message');
    t.assertJsonEquals({}, {}, 'message');
    t.assertNull(undefined, 'message');
    t.assertNotNull(1, 'message');
};

exports.testAssertThrows = function () {
    const s = t.assertThrows(() => {throw 'string!';}, Java.type('java.lang.String'));
    t.assertEquals('string!', s);

    const e = t.assertThrows(() => {throw new Error('message');});
    t.assertEquals('message', e.message);

    t.assertThrows(() => {t.assertThrows(() => {});});
};