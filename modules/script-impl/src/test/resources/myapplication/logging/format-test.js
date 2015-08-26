var assert = Java.type('org.junit.Assert');

var json1 = {};

var s1 = log.format('simple message');
assert.assertEquals('(/logging/format-test.js) simple message', s1);

var s2 = log.format('param %s and %s', 1, 2);
assert.assertEquals('(/logging/format-test.js) param 1 and 2', s2);

var s3 = log.format('empty json %s', JSON.stringify(json1));
assert.assertEquals('(/logging/format-test.js) empty json {}', s3);

var json2 = {
    a: 1,
    b: [1, 2],
    c: {
        a: 1,
        b: [1, 2]
    }
};

var s4 = log.format('complex json %s', JSON.stringify(json2));
s4 = s4.replace(/\n/g, '');
assert.assertEquals('(/logging/format-test.js) complex json {"a":1,"b":[1,2],"c":{"a":1,"b":[1,2]}}', s4);

var s5 = log.format('empty json %s', json1);
assert.assertEquals('(/logging/format-test.js) empty json {}', s5);

var s6 = log.format('complex json %s', json2);
assert.assertEquals('(/logging/format-test.js) complex json {"a":1,"b":[1,2],"c":{"a":1,"b":[1,2]}}', s6);

var array1 = [];

var s7 = log.format('empty array %s', array1);
assert.assertEquals('(/logging/format-test.js) empty array []', s7);

var array2 = [1, 2];

var s8 = log.format('simple array %s', array2);
assert.assertEquals('(/logging/format-test.js) simple array [1,2]', s8);

var array3 = [1, json1];

var s9 = log.format('complex array %s', array3);
assert.assertEquals('(/logging/format-test.js) complex array [1,{}]', s9);
