module api_model {

    export interface ContentSummaryExtModel extends ExtModel {
        data:{
            id:string;
            name:string;
            path:string;
            type:string;
            displayName:string;
            owner:string;
            modifier:string;
            iconUrl:string;
            modifiedTime:Date;
            createdTime:Date;
            editable:boolean;
            deletable:boolean;
            allowsChildren:boolean;
            hasChildren:boolean;
            leaf:boolean;
        };
    }
}