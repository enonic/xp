module api.content{

    export class ContentSummary extends api.item.BaseItem implements api.node.Node {

        private contentId:ContentId;

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

        static fromJsonArray(jsonArray:api.content.json.ContentSummaryJson[]):ContentSummary[] {
            var array:ContentSummary[] = [];
            jsonArray.forEach((json:api.content.json.ContentSummaryJson) => {
                array.push(new ContentSummary(json));
            });
            return array;
        }

        constructor(json:api.content.json.ContentSummaryJson) {
            super(json);
            this.contentId = new ContentId( json.id );
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
        }

        getContentId():ContentId {
            return this.contentId;
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

    }
}