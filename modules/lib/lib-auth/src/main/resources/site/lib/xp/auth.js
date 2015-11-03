function required(params, name) {
    var value = params[name];
    if (value === undefined) {
        throw "Parameter '" + name + "' is required";
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

exports.createUser = function (userStore, name, displayName, email) {
    var bean = __.newBean('com.enonic.xp.lib.auth.CreateUserHandler');

    bean.userStore = __.nullOrValue(userStore);

    bean.name = __.nullOrValue(name);

    bean.displayName = __.nullOrValue(displayName);

    bean.email = __.nullOrValue(email);

    return __.toNativeObject(bean.createUser());
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

