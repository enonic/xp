var t = require('/lib/xp/testing.js');
var auth = require('/lib/xp/auth.js');

exports.hasWriteAndRead = function () {
    t.assertEquals(true, auth.hasScope('com.example.orders:write'));
    t.assertEquals(true, auth.hasScope('com.example.orders:read'));
    t.assertEquals(true, auth.hasScope('openid'));
    t.assertEquals(false, auth.hasScope('read:content'));
};

exports.hasReadOnly = function () {
    t.assertEquals(true, auth.hasScope('com.example.orders:read'));
    t.assertEquals(false, auth.hasScope('com.example.orders:write'));
};

exports.hasNone = function () {
    t.assertEquals(false, auth.hasScope('com.example.orders:read'));
};

exports.notLimited = function () {
    t.assertEquals(true, auth.hasScope('com.example.orders:write'));
};
