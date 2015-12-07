// Returns the current loggedin user.
var user = auth.getUser();

if (user) {
    log.info('User logged in: %s', user.displayName);
}
