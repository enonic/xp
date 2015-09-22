var assert = require('/lib/xp/assert.js');
var auth = require('/lib/xp/auth.js');

exports.hasRole = function () {

    var result = auth.hasRole('my-role');

    assert.assertEquals('hasRole result should be true', true, result);

};

exports.hasRoleByKey = function () {

    var result = auth.hasRole('role:my-role');

    assert.assertEquals('hasRole result should be true', true, result);

};

exports.doesNotHaveRole = function () {

    //var result = auth.hasRole('my-role');
    var result = auth.hasRole();

    assert.assertEquals('hasRole result should be false', false, result);

};
