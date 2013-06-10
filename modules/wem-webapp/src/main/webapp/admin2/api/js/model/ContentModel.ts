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
            createdTime:Date;
            modifiedTime:Date;

            editable:bool;
            deletable:bool;
            hasChildren:bool;
            allowsChildren:bool;
        };
    }
}