module api_model {

    export interface ContentModel extends Model {
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
            editable:bool;
            deletable:bool;
            allowsChildren:bool;
            hasChildren:bool;
            leaf:bool;
        };
    }
}