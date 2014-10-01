var assert = Java.type('org.junit.Assert');

var content = executeCommand('com.enonic.wem.portal.content.GetContentById', {
    id: '123'
});

assert.assertNotNull(content);
assert.assertEquals('My Content', content.displayName);