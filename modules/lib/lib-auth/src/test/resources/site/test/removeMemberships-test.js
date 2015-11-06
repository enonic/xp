var assert = require('/lib/xp/assert.js');
var auth = require('/lib/xp/auth.js');

exports.removeGroupAndRoleMembershipsFromUser = function () {

    auth.removeMemberships('user:myUserStore:userId', ['role:roleId', 'group:myGroupStore:groupId']);
};

exports.removeRoleMembershipsFromGroup = function () {

    auth.removeMemberships('group:myGroupStore:groupId', ['role:roleId-1', 'role:roleId-2']);
};

exports.removeMembershipsEmptyList = function () {

    auth.removeMemberships('group:myGroupStore:groupId', []);
};