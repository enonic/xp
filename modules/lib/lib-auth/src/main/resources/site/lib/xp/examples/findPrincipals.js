// Find principals with the specified name.
var result = authLib.findPrincipals({
    type: 'user',
    userStore: 'user-store',
    start: 0,
    count: 10,
    name: 'user1'
});
