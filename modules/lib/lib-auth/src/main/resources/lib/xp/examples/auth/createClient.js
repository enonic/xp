var authLib = require('/lib/xp/auth');
var t = require('/lib/xp/testing');

// BEGIN
// Registers an MCP client with the system ID provider, limited to one scope and one resource.
var client = authLib.createClient({
    idProvider: 'system',
    id: 'claude',
    displayName: 'Claude',
    uri: 'https://claude.ai',
    grantTypes: ['authorization_code', 'refresh_token'],
    redirectUris: ['https://claude.ai/api/mcp/auth_callback'],
    scope: ['content:read'],
    resource: ['https://example.com/api/mcp'],
    grantTtl: 'P30D'
});
// END

// BEGIN
// The client as registered.
var expected = {
    'idProvider': 'system',
    'id': 'claude',
    'displayName': 'Claude',
    'uri': 'https://claude.ai',
    'grantTypes': ['authorization_code', 'refresh_token'],
    'redirectUris': ['https://claude.ai/api/mcp/auth_callback'],
    'scope': ['content:read'],
    'resource': ['https://example.com/api/mcp'],
    'grantTtl': 'PT720H',
    'enabled': true
};
// END

t.assertJsonEquals(expected, client);
