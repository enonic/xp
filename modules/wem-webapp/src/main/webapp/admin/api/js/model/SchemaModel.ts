module api_model {

    export interface SchemaExtModel extends ExtModel {
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