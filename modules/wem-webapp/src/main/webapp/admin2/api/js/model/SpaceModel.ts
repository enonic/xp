Ext.define('Admin.model.SpaceModel', {
    extend: 'Ext.data.Model',

    fields: <any[]>[
        'name', 'displayName', 'iconUrl', 'rootContentId',
        {name: 'createdTime', type: 'date', default: new Date()},
        {name: 'modifiedTime', type: 'date', default: new Date()},
        { name: 'editable', type: 'boolean' },
        { name: 'deletable', type: 'boolean' },
    ],

    idProperty: 'name'
});

module api_model {

    export interface SpaceModel extends Model {
        data:{
            name:string;
            displayName:string;
            iconUrl:string;
            rootContentId:number;
            createdTime:Date;
            modifiedTime:Date;

            editable:bool;
            deletable:bool;
        };
    }
}