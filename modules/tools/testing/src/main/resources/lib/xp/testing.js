/* global __, Java*/

var helper = Java.type('com.enonic.xp.testing.helper.TestHelper');

/**
 * Load a file from path.
 *
 * @param {String} path Full path to file to load.
 */
exports.load = function (path) {
    return helper.load(path);
};

/**
 * Run script.
 *
 * @param {String} path Full path to script.
 */
exports.runScript = function (path) {
    testInstance.runScript(path);
};

/**
 * Mock a library for require.
 *
 * @param {String} path Full path for the require library.
 * @param object The actual mocked object.
 */
exports.mock = function (path, object) {
    __.registerMock(path, object);
};

/**
 * Assert that the value is true.
 *
 * @param actual Actual value to test.
 * @param message Optional message.
 */
exports.assertTrue = function (actual, message) {
    helper.assertTrue(actual, message || '');
};

/**
 * Assert that the value is false.
 *
 * @param actual Actual value to test.
 * @param message Optional message.
 */
exports.assertFalse = function (actual, message) {
    helper.assertFalse(actual, message || '');
};

/**
 * Assert that the expected == actual.
 *
 * @param expected Expected value.
 * @param actual Actual value to test.
 * @param message Optional message.
 */
exports.assertEquals = function (expected, actual, message) {
    helper.assertEquals(expected, actual, message || '');
};

/**
 * Assert that the expected != actual.
 *
 * @param expected Expected value.
 * @param actual Actual value to test.
 * @param message Optional message.
 */
exports.assertNotEquals = function (expected, actual, message) {
    helper.assertNotEquals(expected, actual, message || '');
};

/**
 * Assert that the JSON expected == actual.
 *
 * @param expected Expected value.
 * @param actual Actual value to test.
 * @param message Optional message.
 */
exports.assertJson = function (expected, actual, message) {
    var expectedJson = JSON.stringify(expected, replaceJava, 2);
    var actualJson = JSON.stringify(actual, replaceJava, 2);
    helper.assertEquals(expectedJson, actualJson, message || '');
};

function replaceJava(key, value) {
    if (value instanceof Java.type('com.google.common.io.ByteSource')) {
        return {};
    }
    return value;
}

/**
 * Assert that the JSON expected == actual.
 *
 * @param expected Expected value.
 * @param actual Actual value to test.
 * @param message Optional message.
 */
exports.assertJsonEquals = function (expected, actual, message) {
    exports.assertJson(expected, actual, message);
};

/**
 * Assert not null.
 */
exports.assertNull = function (value, message) {
    helper.assertTrue(!value || (value === null), message || '');
};

/**
 * Assert not null.
 */
exports.assertNotNull = function (value, message) {
    helper.assertTrue(!!value || (value !== null), message || '');
};

/**
 * Assert that function throws an exception.
 * @param {function} fn executable function that should throw an exception when called.
 * @returns {*} thrown exception
 */
exports.assertThrows = function (fn) {
    try {
        fn();
    } catch (e) {
        return e;
    }
    helper.fail( 'Nothing was thrown' );
};
