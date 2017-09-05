var auth = require('/lib/xp/auth.js');

exports.addUserAndGroupMembersToRole = function () {

    auth.addMembers('role:roleId', ['user:mystore:user1', 'group:mystore:group1']);
};

exports.addGroupMemberToRole = function () {

    auth.addMembers('role:roleId', ['group:mystore:group1']);
};

exports.addMembersEmptyList = function () {

    auth.addMembers('group:mystore:group1', []);
};
