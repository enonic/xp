var assert = Java.type('org.junit.Assert');
var assertHelper = Java.type('com.enonic.xp.testing.AssertHelper');

function toJson(object) {
    return JSON.stringify(object, null, 2);
}

function assertEquals() {
    if (arguments.length == 3) {
        assertHelper.assertEquals(arguments[0], arguments[1], arguments[2]);
    } else {
        assertHelper.assertEquals(arguments[0], arguments[1]);
    }
}

function assertJsonEquals() {
    if (arguments.length == 3) {
        assert.assertEquals(arguments[0], toJson(arguments[1]), toJson(arguments[2]));
    } else {
        assert.assertEquals(toJson(arguments[0]), toJson(arguments[1]));
    }
}

function assertNull() {
    if (arguments.length == 2) {
        assert.assertNull(arguments[0], arguments[1]);
    } else {
        assert.assertNull(arguments[0]);
    }
}

function assertNotNull() {
    assert.assertNotNull(arguments[0]);
}

function assertTrue() {
    if (arguments.length == 2) {
        assert.assertTrue(arguments[0], arguments[1]);
    } else {
        assert.assertTrue(arguments[0]);
    }
}

exports.assertEquals = assertEquals;
exports.assertJsonEquals = assertJsonEquals;
exports.assertNull = assertNull;
exports.assertNotNull = assertNotNull;
exports.assertTrue = assertTrue;
