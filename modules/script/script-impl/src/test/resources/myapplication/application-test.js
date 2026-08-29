var assert = Java.type('org.junit.jupiter.api.Assertions');

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
assert.assertEquals('dotted.key,key', Object.keys(app.config).sort().join(','));
assert.assertEquals(true, Object.prototype.hasOwnProperty.call(app.config, 'key'));
assert.assertEquals('value', app.config['key']);
assert.assertEquals('dotted value', app.config['dotted.key']);

var rebindFailed = false;
try {
    app = 'replaced';
} catch (e) {
    rebindFailed = true;
}
assert.assertEquals(true, rebindFailed);
assert.assertEquals('myapplication', app.name);
