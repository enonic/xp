Ext.define('Admin.store.contentStudio.BaseTypeTreeStore', {
    extend: 'Ext.data.TreeStore',

    model: 'Admin.model.contentStudio.BaseTypeModel',

    folderSort: true,

    proxy: {
        type: 'direct',
        directFn: Admin.lib.RemoteService.baseType_tree,
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'baseTypes',
            totalProperty: 'total'
        }
    }

});