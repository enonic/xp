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
