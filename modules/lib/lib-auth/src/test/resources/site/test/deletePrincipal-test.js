var t = require('/lib/xp/testing.js');
var auth = require('/lib/xp/auth.js');

exports.deleteUser = function () {
    var result = auth.deletePrincipal('user:myUserStore:userId');
    t.assertJsonEquals(true, result);
};

exports.deleteNonExistingUser = function () {
    var result = auth.deletePrincipal('user:myUserStore:XXX');
    t.assertEquals(false, result);
};

exports.deleteSystemUser = function () {
    var result = auth.deletePrincipal('user:system:su');
    t.assertEquals(false, result);
};