module api_remote {

    export interface Space {
        createdTime:Date;
        deletable:bool;
        displayName:string;
        editable:bool;
        iconUrl:string;
        modifiedTime:Date;
        name:string;
        rootContentId:string;
    }

    export interface SpaceLight {
        createdTime:Date;
        displayName:string;
        iconUrl:string;
        modifiedTime:Date;
        name:string;
        rootContentId:string;
    }

}