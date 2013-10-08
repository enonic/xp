module api_content{

    export class ContentSummary {

        private id:string;

        private name:string;

        private displayName:string;

        private path:ContentPath;

        private root:boolean;

        private children:boolean;

        private type:string;

        private iconUrl:string;

        private createdTime:Date;

        private modifiedTime:Date;

        private modifier:string;

        private owner:string;

        private editable:boolean;

        private deletable:boolean;

        constructor(json:api_content_json.ContentSummaryJson) {
            this.id = json.id;
            this.name = json.name;
            this.displayName = json.displayName;
            this.path = ContentPath.fromString(json.path);
            this.root = json.root;
            this.children = json.hasChildren;
            this.type = json.type;
            this.iconUrl = json.iconUrl;
            this.createdTime = new Date(json.createdTime);
            this.modifiedTime = new Date(json.modifiedTime);
            this.modifier = json.modifier;
            this.owner = json.owner;
            this.deletable = json.deletable;
            this.editable = json.editable;
        }

        getId():string {
            return this.id;
        }

        getName():string {
            return this.name;
        }

        getDisplayName():string {
            return this.displayName;
        }

        getPath():ContentPath {
            return this.path;
        }

        isRoot():boolean {
            return this.root;
        }

        hasChildren():boolean {
            return this.children;
        }

        getType():string {
            return this.type;
        }

        getIconUrl():string {
            return this.iconUrl;
        }

        getCreatedTime():Date {
            return this.createdTime;
        }

        getModifiedTime():Date {
            return this.modifiedTime;
        }

        getOwner():string {
            return this.owner;
        }

        getModifier():string {
            return this.modifier;
        }

        isDeletable():boolean {
            return this.deletable;
        }

        isEditable():boolean {
            return this.editable;
        }
    }
}