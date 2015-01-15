var s1 = log.format('simple message');
assert.assertEquals('simple message', s1);

var s2 = log.format('param %s and %s', 1, 2);
assert.assertEquals('param 1 and 2', s2);

var s3 = log.format('empty json %s', {});
assert.assertEquals('empty json {}', s3);

var s4 = log.format('complex json %s', {
    a: 1,
    b: [1, 2],
    c: {
        a: 1,
        b: [1, 2]
    }
});
s4 = s4.replace(/\n/g, '');
assert.assertEquals('complex json {"a":1,"b":[1,2],"c":{"a":1,"b":[1,2]}}', s4);
