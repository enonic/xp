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

exports.get = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.content.GetContentHandler');
    bean.key = required(params, 'key');
    bean.branch = nullOrValue(params.branch);
    return __.toNativeObject(bean.execute());
};

exports.delete = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.content.DeleteContentHandler');
    bean.key = required(params, 'key');
    bean.branch = nullOrValue(params.branch);
    return bean.execute();
};

exports.getChildren = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.content.GetChildContentHandler');
    bean.key = required(params, 'key');
    bean.branch = nullOrValue(params.branch);
    bean.start = params.start;
    bean.count = params.count;
    bean.sort = nullOrValue(params.sort);
    return __.toNativeObject(bean.execute());
};

exports.create = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.content.CreateContentHandler');
    bean.name = nullOrValue(params.name);
    bean.parentPath = nullOrValue(params.parentPath);
    bean.displayName = nullOrValue(params.displayName);
    bean.contentType = nullOrValue(params.contentType);
    bean.requireValid = params.requireValid;

    bean.data = __.toScriptValue(params.data);
    bean.x = __.toScriptValue(params.x);

    return __.toNativeObject(bean.execute());
};

exports.query = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.content.QueryContentHandler');
    bean.branch = nullOrValue(params.branch);
    bean.start = params.start;
    bean.count = params.count;
    bean.query = nullOrValue(params.query);
    bean.sort = nullOrValue(params.sort);
    bean.aggregations = __.toScriptValue(params.aggregations);
    bean.contentTypes = __.toScriptValue(params.contentTypes);
    return __.toNativeObject(bean.execute());
};

exports.modify = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.content.ModifyContentHandler');
    bean.key = required(params, 'key');
    bean.branch = nullOrValue(params.branch);
    bean.editor = __.toScriptValue(params.editor);
    return __.toNativeObject(bean.execute());
};
