var authLib = require('/lib/xp/auth');

var createIdProviderResult = authLib.createIdProvider({
    key: 'myIdProvider',
    displayName: 'My id provider',
    description: 'My id provider description',
    permissions: [
        {
            principal: 'role:system.admin',
            access: 'ADMINISTRATOR'
        }
    ]
});
