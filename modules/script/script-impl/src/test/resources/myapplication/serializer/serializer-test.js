var assert = Java.type('org.junit.Assert');

exports.testObject = function (o) {
    assert.assertEquals('{"a":1,"b":"2","c":3,"d":4}', JSON.stringify(o));
};

exports.testArray = function (o) {
    assert.assertEquals('[{"a":1,"b":2},2]', JSON.stringify(o));
};

exports.testMapValue = function (o) {
    assert.assertEquals('{"map":{"value2":true,"value1":1,"value4":{"child2":2,"child1":1},"value3":"string"},"b":2}',
        JSON.stringify(o));
};

exports.testListValue = function (o) {
    assert.assertEquals('{"root":[1,true,"string",["list1A","list1B",["list2A","list2B"]],{"mapValue":1},{"multimapValue":[1,2]}]}',
        JSON.stringify(o));
};

exports.testMultimapValue = function (o) {
    assert.assertEquals('{"multimap":{"value2":[false,true],"value1":[1,2,3],"value4":{"children":[5,6,7]},"value3":"string1"}}',
        JSON.stringify(o));
};
