var assert = Java.type('org.junit.jupiter.api.Assertions');

exports.testArray = function (value) {
    assert.assertEquals('Array should be instanceof Array', true, value instanceof Array);
    return JSON.stringify(value);
};

exports.testObject = function (value) {
    return JSON.stringify(value);
};
