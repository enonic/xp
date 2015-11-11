var assert = require('/lib/xp/assert.js');
var auth = require('/lib/xp/auth.js');

exports.addUserAndGroupMembersToRole = function () {

    auth.addMembers('role:roleId', ['user:myUserStore:userId', 'group:myGroupStore:groupId']);
};

exports.addGroupMemberToRole = function () {

    auth.addMembers('role:roleId', ['group:myGroupStore:groupId']);
};

exports.addMembersEmptyList = function () {

    auth.addMembers('group:myGroupStore:groupId', []);
};