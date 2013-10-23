module api_model {

    //TODO: deprecated, use domain Schema object instead outside grid
    export interface SchemaExtModel extends Ext_data_Model {
        data: {
            key:string;
            name:string;
            qualifiedName:string;
            displayName:string;
            type:string;
            createdTime:Date;
            modifiedTime:Date;
            iconUrl:string;
        };
    }
}