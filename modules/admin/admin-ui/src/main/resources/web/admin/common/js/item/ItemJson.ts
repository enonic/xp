module api.item{

    export interface ItemJson {

        id: string;

        createdTime: string;

        modifiedTime: string;

        editable: boolean;

        deletable: boolean;
    }

}