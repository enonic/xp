module api_remote {

    export interface Schema {
        key:string;
        name:string;
        module:string;
        qualifiedName:string;
        displayName:string;
        type:string;
        createdTime:Date;
        modifiedTime:Date;
        iconUrl:string;
    }
}