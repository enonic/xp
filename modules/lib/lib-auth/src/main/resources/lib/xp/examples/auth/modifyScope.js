var authLib = require('/lib/xp/auth');
var t = require('/lib/xp/testing');

// BEGIN
// Reword a scope and make it one the user cannot drop.
var scope = authLib.modifyScope({
    idProvider: 'system',
    id: 'content:write',
    description: 'Create, change and publish content',
    required: true
});
// END

t.assertJsonEquals({
    'idProvider': 'system',
    'id': 'content:write',
    'title': 'Edit content',
    'description': 'Create, change and publish content',
    'roles': ['role:cms.cm.app', 'role:cms.expert'],
    'required': true
}, scope);
