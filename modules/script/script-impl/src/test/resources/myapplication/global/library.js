var assert = Java.type('org.junit.jupiter.api.Assertions');

exports.testArray = function (value) {
    assert.assertTrue(value instanceof Array, 'Array should be instanceof Array');
    return JSON.stringify(value);
};

exports.testObject = function (value) {
    return JSON.stringify(value);
};
