module api.content{

    export class ContentSummary extends ContentIdBaseItem implements api.node.Node {

        private id:string;

        private name:ContentName;

        private displayName:string;

        private path:ContentPath;

        private root:boolean;

        private children:boolean;

        private type:api.schema.content.ContentTypeName;

        private iconUrl:string;

        private modifier:string;

        private owner:string;

        private site:boolean;

        private page:boolean;

        private draft:boolean;

        private createdTime:Date;

        private modifiedTime:Date;

        private deletable:boolean;

        private editable:boolean;

        static fromJsonArray(jsonArray:api.content.json.ContentSummaryJson[]):ContentSummary[] {
            var array:ContentSummary[] = [];
            jsonArray.forEach((json:api.content.json.ContentSummaryJson) => {
                array.push(new ContentSummary(json));
            });
            return array;
        }

        constructor(json:api.content.json.ContentSummaryJson) {
            super(json);
            this.name = ContentName.fromString(json.name);
            this.displayName = json.displayName;
            this.path = ContentPath.fromString(json.path);
            this.root = json.isRoot;
            this.children = json.hasChildren;
            this.type = new api.schema.content.ContentTypeName(json.type);
            this.iconUrl = json.iconUrl;
            this.modifier = json.modifier;
            this.owner = json.owner;
            this.site = json.isSite;
            this.page = json.isPage;
            this.draft = json.draft;

            this.id = json.id;
            this.createdTime = new Date(json.createdTime);
            this.modifiedTime = new Date(json.modifiedTime);
            this.deletable = json.deletable;
            this.editable = json.editable;
        }

        getName():ContentName {
            return this.name;
        }

        getDisplayName():string {
            return this.displayName;
        }

        hasParent():boolean {
            return this.path.hasParentContent();
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

        getType():api.schema.content.ContentTypeName {
            return this.type;
        }

        getIconUrl():string {
            return this.iconUrl;
        }

        getOwner():string {
            return this.owner;
        }

        getModifier():string {
            return this.modifier;
        }

        isSite():boolean {
            return this.site;
        }

        isPage():boolean {
            return this.page;
        }

        isDraft():boolean {
            return this.draft;
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