var authLib = require('/lib/xp/auth');

// BEGIN
// Add members to specified principal.
authLib.addMembers('role:roleId', ['user:mystore:user1', 'group:mystore:group1']);
// END
