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
    bean.userStore = required(params, 'userStore');

    return __.toNativeObject(bean.login());
};

exports.logout = function () {
    // TODO: Implement
};

exports.getUser = function () {
    var bean = __.newBean('com.enonic.xp.lib.auth.GetUserHandler');

    return __.toNativeObject(bean.getUser());
};

exports.hasRole = function (roleKey) {
    // TODO: Implement
};

