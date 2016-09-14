var assert = Java.type('org.junit.Assert');

exports.testObject = function (o) {
    assert.assertEquals('{"a":1,"b":"2","c":3,"d":4}', JSON.stringify(o));
};

exports.testArray = function (o) {
    assert.assertEquals('[{"a":1,"b":2},2]', JSON.stringify(o));
};
