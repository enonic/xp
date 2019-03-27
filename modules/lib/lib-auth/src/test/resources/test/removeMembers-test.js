var auth = require('/lib/xp/auth.js');

exports.removeMembersFromUser = function () {

    auth.removeMembers('user:myIdProvider:userId', ['role:roleId', 'group:myGroupStore:groupId']);
};

exports.removeMembersFromRole = function () {

    auth.removeMembers('role:roleId', ['user:myIdProvider:userId', 'group:myGroupStore:groupId']);
};

exports.removeMembersFromGroup = function () {

    auth.removeMembers('group:myGroupStore:groupId', ['user:myIdProvider:userId', 'group:myGroupStore:groupId2']);
};

exports.removeMembersEmptyList = function () {

    auth.removeMembers('group:myGroupStore:groupId', []);

};