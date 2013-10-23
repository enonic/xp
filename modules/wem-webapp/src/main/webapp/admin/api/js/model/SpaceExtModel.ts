module api_model {

    //TODO: deprecated, use domain Space object instead outside grid
    export interface SpaceExtModel extends Ext_data_Model {
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