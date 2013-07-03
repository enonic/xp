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

    export interface RemoteCallSchemaListParams {
        types:string[];
        search:string;
        modules:string[];
    }

    export interface RemoteCallSchemaListResult extends RemoteCallResultBase {
        schemas:Schema[];
    }

    export interface RemoteCallGetSchemaTreeParams {
        types:string[];
    }

    export interface RemoteCallGetSchemaTreeResult extends RemoteCallResultBase {
        schemas:SchemaTreeNode[];
        total:number;
    }

}