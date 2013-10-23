module api_item{

    export interface Item {

        getId():string;

        getCreatedTime():Date;

        getModifiedTime():Date;

        isDeletable():boolean;

        isEditable():boolean;
    }

}