var authLib = require('/lib/xp/auth');

// BEGIN
// Remove members from specified principal.
authLib.removeMembers('role:roleId', ['user:mystore:user1', 'group:mystore:group1']);
// END
