exports.testObject = function (o) {
    assert.assertEquals('{"a":1,"b":2}', JSON.stringify(o));
};

exports.testArray = function (o) {
    assert.assertEquals('[{"a":1,"b":2},2]', JSON.stringify(o));
};
