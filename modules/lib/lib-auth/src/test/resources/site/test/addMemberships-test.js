var assert = require('/lib/xp/assert.js');
var auth = require('/lib/xp/auth.js');

exports.addGroupAndRoleMembershipsToUser = function () {

    auth.addMemberships('user:myUserStore:userId', ['role:roleId', 'group:myGroupStore:groupId']);
};

exports.addRoleMembershipsToGroup = function () {

    auth.addMemberships('group:myGroupStore:groupId', ['role:roleId-1', 'role:roleId-2']);
};

exports.addMembershipsEmptyList = function () {

    auth.addMemberships('group:myGroupStore:groupId', []);
};