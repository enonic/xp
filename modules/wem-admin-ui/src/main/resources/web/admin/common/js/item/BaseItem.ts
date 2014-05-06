module api.item {

    export class BaseItem implements Item, api.Equitable {

        private id: string;

        private createdTime: Date;

        private modifiedTime: Date;

        private deletable: boolean;

        private editable: boolean;

        constructor(builder: BaseItemBuilder) {
            this.id = builder.id;
            this.createdTime = builder.createdTime;
            this.modifiedTime = builder.modifiedTime;
            this.deletable = builder.deletable;
            this.editable = builder.editable;
        }

        getId(): string {
            return this.id;
        }

        getCreatedTime(): Date {
            return this.createdTime;
        }

        getModifiedTime(): Date {
            return this.modifiedTime;
        }

        isDeletable(): boolean {
            return this.deletable;
        }

        isEditable(): boolean {
            return this.editable;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, BaseItem)) {
                return false;
            }

            var other = <BaseItem>o;

            if (!ObjectHelper.stringEquals(this.id, other.id)) {
                return false;
            }

            if (!ObjectHelper.dateEquals(this.createdTime, other.createdTime)) {
                return false;
            }

            if (!ObjectHelper.dateEquals(this.modifiedTime, other.modifiedTime)) {
                return false;
            }

            if (!ObjectHelper.booleanEquals(this.deletable, other.deletable)) {
                return false;
            }

            if (!ObjectHelper.booleanEquals(this.editable, other.editable)) {
                return false;
            }

            return true;
        }
    }

    export class BaseItemBuilder {

        id: string;

        createdTime: Date;

        modifiedTime: Date;

        deletable: boolean;

        editable: boolean;

        constructor(source?: BaseItem) {
            if (source) {
                this.id = source.getId();
                this.createdTime = source.getCreatedTime();
                this.modifiedTime = source.getModifiedTime();
                this.deletable = source.isDeletable();
                this.editable = source.isEditable();
            }
        }

        fromBaseItemJson(json: ItemJson, idProperty: string = 'id'): BaseItemBuilder {

            this.id = json[idProperty];
            this.createdTime = json.createdTime ? new Date(json.createdTime) : null;
            this.modifiedTime = json.modifiedTime ? new Date(json.modifiedTime) : null;
            this.deletable = json.deletable;
            this.editable = json.editable;
            return this;
        }

        build(): BaseItem {
            return new BaseItem(this);
        }
    }

}