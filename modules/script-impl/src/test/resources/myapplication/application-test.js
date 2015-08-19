var assert = Java.type('org.junit.Assert');

assert.assertEquals('myapplication:/application-test.js', module.id);
assert.assertEquals(true, exports == module.exports);

exports.val = '1';
assert.assertEquals('1', exports.val);
assert.assertEquals('1', module.exports.val);

module.exports.val = '2';
assert.assertEquals('2', exports.val);
assert.assertEquals('2', module.exports.val);

assert.assertEquals('myapplication', app.name);
assert.assertEquals('1.0.0', app.version);
