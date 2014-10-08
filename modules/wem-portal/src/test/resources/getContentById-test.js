var assert = Java.type('org.junit.Assert');

var content = execute('content.getById', {
    id: '123'
});

assert.assertNotNull(content);
assert.assertEquals('My Content', content.displayName);