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

// GraalJS enforces the read-only contract at runtime; on Nashorn the types are the only guard
if (typeof Graal !== 'undefined') {
    var messageOf = function (action) {
        try {
            action();
        } catch (e) {
            return e.message;
        }
        return 'nothing thrown';
    };

    var nameOf = function (action) {
        try {
            action();
        } catch (e) {
            return e.name;
        }
        return 'nothing thrown';
    };

    assert.assertEquals(true, Object.isFrozen(app));
    assert.assertEquals(true, Object.isFrozen(app.config));

    // the message must not serialize the object: app.config carries the deployment's secrets
    assert.assertEquals("Cannot assign to read only property 'app' of global", messageOf(function () {
        app = 'replaced';
    }));
    assert.assertEquals("Cannot assign to read only property 'name' of app", messageOf(function () {
        app.name = 'replaced';
    }));
    assert.assertEquals("Cannot assign to read only property 'key' of app.config", messageOf(function () {
        app.config['key'] = 'replaced';
    }));

    assert.assertEquals('TypeError', nameOf(function () {
        app.config['brandNew'] = 'added';
    }));
    assert.assertEquals('TypeError', nameOf(function () {
        delete app.config['key'];
    }));
    assert.assertEquals('TypeError', nameOf(function () {
        Object.defineProperty(app, 'name', {value: 'replaced'});
    }));

    assert.assertEquals('myapplication', app.name);
    assert.assertEquals('value', app.config['key']);
    assert.assertEquals('undefined', typeof app.config['brandNew']);
}
