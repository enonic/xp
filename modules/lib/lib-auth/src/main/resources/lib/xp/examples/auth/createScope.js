var authLib = require('/lib/xp/auth');
var t = require('/lib/xp/testing');

// BEGIN
// Adds a scope to the system ID provider.
var scope = authLib.createScope({
    idProvider: 'system',
    id: 'content:write',
    title: 'Edit content',
    description: 'Create, change and publish content in all projects',
    roles: ['role:cms.cm.app', 'role:cms.expert']
});
// END

// BEGIN
// The scope as stored.
var expected = {
    'idProvider': 'system',
    'id': 'content:write',
    'title': 'Edit content',
    'description': 'Create, change and publish content in all projects',
    'roles': ['role:cms.cm.app', 'role:cms.expert'],
    'required': false
};
// END

t.assertJsonEquals(expected, scope);
