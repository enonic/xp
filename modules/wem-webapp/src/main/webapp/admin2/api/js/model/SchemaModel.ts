module api_model {

    export interface SchemaModel extends Model {
        data: {
            key:string;
            name:string;
            module:string;
            qualifiedName:string;
            displayName:string;
            type:string;
            createdTime:Date;
            modifiedTime:Date;
            iconUrl:string;
        };
    }
}