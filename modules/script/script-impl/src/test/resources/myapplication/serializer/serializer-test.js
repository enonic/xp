var assert = Java.type('org.junit.jupiter.api.Assertions');

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

exports.testNumber = function (o) {
    assert.assertTrue(typeof o['longEnough'] === 'number');
    assert.assertTrue(typeof o['longAsInt'] === 'number');
    assert.assertTrue(typeof o['integer'] === 'number');
    assert.assertTrue(typeof o['float'] === 'number');
    assert.assertTrue(typeof o['double'] === 'number');
    assert.assertTrue(typeof o['short'] === 'number');
    assert.assertTrue(typeof o['byte'] === 'number');

    assert.assertTrue(o['longAsInt'] === 42);
    assert.assertTrue(o['longEnough'] === 2147483648);

    // Exact values for standard types
    assert.assertTrue(o['integer'] === 2147483647);
    assert.assertTrue(o['short'] === 32767);
    assert.assertTrue(o['byte'] === 127);

    // BigInteger and BigDecimal and not JS safe integer pass through as-is (not converted)
    assert.assertTrue(typeof o['maxLong'] === 'object');
    assert.assertTrue(typeof o['bigInteger'] === 'object');
    assert.assertTrue(typeof o['bigDecimal'] === 'object');
};