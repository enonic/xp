var assert = require('/lib/xp/assert.js');
var auth = require('/lib/xp/auth.js');

exports.removeMembersFromUser = function () {

    auth.removeMembers('user:myUserStore:userId', ['role:roleId', 'group:myGroupStore:groupId']);
};

exports.removeMembersFromRole = function () {

    auth.removeMembers('role:roleId', ['user:myUserStore:userId', 'group:myGroupStore:groupId']);
};

exports.removeMembersFromGroup = function () {

    auth.removeMembers('group:myGroupStore:groupId', ['user:myUserStore:userId', 'group:myGroupStore:groupId2']);
};

exports.removeMembersEmptyList = function () {

    auth.removeMembers('group:myGroupStore:groupId', []);

};