var service = __.getBean('com.enonic.xp.lib.content.ContentServiceWrapper');

exports.get = function (params) {

    var getContent = service.newGetContent();
    getContent.key = params.key;
    getContent.branch = params.branch;

    return getContent.execute();
};

exports.getChildren = function (params) {

    var getContentCildren = service.newGetContentChildren();
    getContentCildren.key = params.key;
    getContentCildren.branch = params.branch;
    getContentCildren.start = params.start;
    getContentCildren.count = params.count;
    getContentCildren.sort = params.sort;

    return getContentCildren.execute();
};

exports.query = function (params) {

    var queryContent = service.newQueryContent();
    queryContent.branch = params.branch;
    queryContent.start = params.start;
    queryContent.count = params.count;
    queryContent.query = params.query;
    queryContent.sort = params.sort;
    queryContent.aggregations = params.aggregations;
    queryContent.contentTypes = params.contentTypes;

    return queryContent.execute();
};

exports.delete = function (params) {

    var deleteContent = service.newDeleteContent();
    deleteContent.key = params.key;
    deleteContent.branch = params.branch;

    return deleteContent.execute();
};

exports.create = function (params) {

    var createContent = service.newCreateContent();
    createContent.key = params.key;
    createContent.name = params.name;
    createContent.parentPath = params.parentPath;
    createContent.displayName = params.displayName;
    createContent.requireValid = params.requireValid;
    createContent.contentType = params.contentType;
    createContent.data = params.data;
    createContent.x = params.x;

    return createContent.execute();
};

exports.modify = function (params) {

    var modifyContent = service.newModifyContent();
    modifyContent.key = params.key;
    modifyContent.branch = params.branch;
    modifyContent.editor = params.editor;
    return modifyContent.execute();

};
