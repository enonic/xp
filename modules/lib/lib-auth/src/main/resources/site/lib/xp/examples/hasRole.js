// Checks if the user has the specified role.
var flag = auth.hasRole('system.admin.login');

log.info('The user ' + (flag ? 'has' : 'does not have') +
         ' access to the admin console.');
