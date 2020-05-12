exports.create = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.project.CreateProjectHandler');
    bean.name = required(params, 'name');
    bean.displayName = nullOrValue(params.displayName);
    bean.description = nullOrValue(params.description);
    bean.language = nullOrValue(params.language);
    bean.permissions = __.toScriptValue(params.permissions);
    bean.readAccess = __.toScriptValue(params.readAccess);
    return __.toNativeObject(bean.execute());
};

exports.modify = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.project.ModifyProjectHandler');
    bean.name = required(params, 'name');
    bean.displayName = nullOrValue(params.displayName);
    bean.description = nullOrValue(params.description);
    bean.language = nullOrValue(params.language);
    return __.toNativeObject(bean.execute());
};

exports.delete = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.project.DeleteProjectHandler');
    bean.name = required(params, 'name');
    return __.toNativeObject(bean.execute());
};

exports.get = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.project.GetProjectHandler');
    bean.name = required(params, 'name');
    return __.toNativeObject(bean.execute());
};

exports.list = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.project.ListProjectsHandler');
    return __.toNativeObject(bean.execute());
};

exports.addPermissions = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.project.AddProjectPermissionsHandler');
    bean.name = required(params, 'name');
    bean.permissions = __.toScriptValue(params.permissions);
    return __.toNativeObject(bean.execute());
};

exports.removePermissions = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.project.RemoveProjectPermissionsHandler');
    bean.name = required(params, 'name');
    bean.permissions = __.toScriptValue(params.permissions);
    return __.toNativeObject(bean.execute());
};

exports.modifyReadAccess = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.project.ModifyProjectReadAccessHandler');
    bean.name = required(params, 'name');
    bean.readAccess = __.toScriptValue(params.readAccess);
    return __.toNativeObject(bean.execute());
};

function required(params, name) {
    var value = params[name];
    if (value === undefined) {
        throw 'Parameter \'' + name + '\' is required';
    }

    return value;
}

function nullOrValue(value) {
    if (value === undefined) {
        return null;
    }

    return value;
}

