Ext.define('Admin.model.schemaManager.ContentTypeModel', {
    extend: 'Ext.data.Model',

    fields: <any[]> [
        'qualifiedName',
        'name',
        'displayName',
        'module',
        { name: 'createdTime', type: 'date', defaultValue: new Date() },
        { name: 'modifiedTime', type: 'date', defaultValue: new Date() },
        'configXML',
        'iconUrl'
    ],

    idProperty: 'qualifiedName'
});

module api_model {

    export interface ContentTypeModel extends Model {
        data:{
            qualifiedName:string;
            name:string;
            displayName:string;
            module:string;
            iconUrl:string;
            configXML:string;
            createdTime:Date;
            modifiedTime:Date;
        };
    }
}