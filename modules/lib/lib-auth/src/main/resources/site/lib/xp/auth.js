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

exports.hasRole = function (roleKey) {
    var bean = __.newBean('com.enonic.xp.lib.auth.HasRoleHandler');

    bean.role = __.nullOrValue(roleKey);

    return bean.hasRole();
};

exports.changePassword = function (password) {
    var bean = __.newBean('com.enonic.xp.lib.auth.ChangePasswordHandler');

    bean.changePassword(password);
}

