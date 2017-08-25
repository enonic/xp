var t = require('/lib/xp/testing.js');
var auth = require('/lib/xp/auth.js');

exports.hasRole = function () {

    var result = auth.hasRole('my-role');

    t.assertEquals(true, result);

};

exports.hasRoleByKey = function () {

    var result = auth.hasRole('role:my-role');

    t.assertEquals(true, result);

};

exports.doesNotHaveRole = function () {

    //var result = auth.hasRole('my-role');
    var result = auth.hasRole();

    t.assertEquals(false, result);

};
