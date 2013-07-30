module api_model {

    export interface ContentTypeExtModel extends ExtModel {
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