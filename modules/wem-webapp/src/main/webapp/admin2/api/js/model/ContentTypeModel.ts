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