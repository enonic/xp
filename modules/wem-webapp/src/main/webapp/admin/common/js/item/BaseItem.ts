module api_item{

    export class BaseItem implements Item {

        private id:string;

        private createdTime:Date;

        private modifiedTime:Date;

        private deletable:boolean;

        private editable:boolean;

        constructor(json:ItemJson, idProperty:string = 'id') {
            this.id = json[idProperty];
            this.createdTime = new Date(json.createdTime);
            this.modifiedTime = new Date(json.modifiedTime);
            this.deletable = json.deletable;
            this.editable = json.editable;
        }

        getId():string {
            return this.id;
        }

        getCreatedTime():Date {
            return this.createdTime;
        }

        getModifiedTime():Date {
            return this.modifiedTime;
        }

        isDeletable():boolean {
            return this.deletable;
        }

        isEditable():boolean {
            return this.editable;
        }
    }

}