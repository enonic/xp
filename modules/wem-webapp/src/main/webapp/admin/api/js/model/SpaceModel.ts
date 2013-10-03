module api_model {

    export interface SpaceExtModel extends ExtModel {
        data:{
            name:string;
            displayName:string;
            iconUrl:string;
            rootContentId:number;
            createdTime:Date;
            modifiedTime:Date;

            editable:boolean;
            deletable:boolean;
        };
    }
}