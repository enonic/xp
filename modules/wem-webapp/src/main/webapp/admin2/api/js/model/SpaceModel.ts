module api_model {

    export interface SpaceModel extends Model {
        data:{
            name:string;
            displayName:string;
            iconUrl:string;
            rootContentId:number;
            createdTime:Date;
            modifiedTime:Date;

            editable:bool;
            deletable:bool;
        };
    }
}