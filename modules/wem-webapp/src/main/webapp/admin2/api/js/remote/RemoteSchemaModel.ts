module api_remote_schema {

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

    export interface SchemaTreeNode {
        key:string;
        name:string;
        module:string;
        qualifiedName:string;
        displayName:string;
        type:string;
        createdTime?:Date;
        modifiedTime?:Date;
        hasChildren:bool;
        schemas:SchemaTreeNode[];
    }

    export interface ListParams {
        types:string[];
        search:string;
        modules:string[];
    }

    export interface ListResult extends api_remote.BaseResult {
        schemas:Schema[];
    }

    export interface GetTreeParams {
        types:string[];
    }

    export interface GetTreeResult extends api_remote.BaseResult {
        schemas:SchemaTreeNode[];
        total:number;
    }

}