function required(params, name) {
    var value = params[name];
    if (value === undefined) {
        throw "Parameter '" + name + "' is required";
    }

    return value;
}

function nullOrValue(value) {
    if (value === undefined) {
        return null;
    }

    return value;
}

exports.login = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.auth.LoginHandler');

    bean.user = required(params, 'user');
    bean.password = required(params, 'password');

    if (params['userStore']) {
        bean.userStore = [].concat(params['userStore']);
    }

    return __.toNativeObject(bean.login());
};

exports.logout = function () {
    var bean = __.newBean('com.enonic.xp.lib.auth.LogoutHandler');

    bean.logout();
};

exports.getUser = function () {
    var bean = __.newBean('com.enonic.xp.lib.auth.GetUserHandler');

    return __.toNativeObject(bean.getUser());
};

exports.getPrincipal = function (principalKey) {
    var bean = __.newBean('com.enonic.xp.lib.auth.GetPrincipalHandler');

    bean.principalKey = __.nullOrValue(principalKey);

    return __.toNativeObject(bean.getPrincipal());
};

exports.getMemberships = function (principalKey) {
    var bean = __.newBean('com.enonic.xp.lib.auth.GetMembershipsHandler');

    bean.principalKey = __.nullOrValue(principalKey);

    return __.toNativeObject(bean.getMemberships());
};

exports.createUser = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.auth.CreateUserHandler');

    bean.userStore = required(params, 'userStore');

    bean.name = required(params, 'name');

    bean.displayName = nullOrValue(params.displayName);

    bean.email = nullOrValue(params.email);

    return __.toNativeObject(bean.createUser());
};

exports.modifyUser = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.auth.ModifyUserHandler');

    bean.principalKey = required(params, 'key');

    bean.editor = __.toScriptValue(params.editor);

    return __.toNativeObject(bean.modifyUser());
};

exports.createGroup = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.auth.CreateGroupHandler');

    bean.userStore = required(params, 'userStore');

    bean.name = required(params, 'name');

    bean.displayName = nullOrValue(params.displayName);

    return __.toNativeObject(bean.createGroup());
};

exports.hasRole = function (roleKey) {
    var bean = __.newBean('com.enonic.xp.lib.auth.HasRoleHandler');

    bean.role = __.nullOrValue(roleKey);

    return bean.hasRole();
};

exports.generatePassword = function () {
    var bean = __.newBean('com.enonic.xp.lib.auth.GeneratePasswordHandler');

    return __.toNativeObject(bean.generatePassword());
}

exports.changePassword = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.auth.ChangePasswordHandler');

    bean.userKey = required(params, 'userKey');
    bean.password = required(params, 'password');

    bean.changePassword();
}

