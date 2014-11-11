var Assert = Java.type('org.junit.Assert');

var s1 = log.format('simple message');
Assert.assertEquals('simple message', s1);

var s2 = log.format('param %s and %s', 1, 2);
Assert.assertEquals('param 1 and 2', s2);

var s3 = log.format('empty json %s', {});
Assert.assertEquals('empty json {}', s3);

var s4 = log.format('complex json %s', {
    a: 1,
    b: [1, 2],
    c: {
        a: 1,
        b: [1, 2]
    }
});
Assert.assertEquals('complex json {"a":"1","b":["1","2"],"c":{"a":"1","b":["1","2"]}}', s4);

var s5 = log.format('complex json %s', {
    a: function (i) {
        return i;
    }
});
Assert.assertEquals('complex json {"a":"function (i) {\\n        return i;\\n    }"}', s5);
