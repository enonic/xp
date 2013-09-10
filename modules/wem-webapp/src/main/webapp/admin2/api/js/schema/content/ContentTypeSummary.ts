module api_schema_content{

    export class ContentTypeSummary {

        private name:string;

        private qualifiedName:string;

        private displayName:string;

        private allowChildren:boolean;

        private abstract:boolean;

        private final:boolean;

        private superType:string;

        private contentDisplayNameScript:string;

        private iconUrl:string;

        private createdTime:Date;

        private modifiedTime:Date;

        private modifier:string;

        private owner:string;

        constructor(json:api_schema_content_json.ContentTypeSummaryJson) {
            this.name = json.name;
            this.qualifiedName = json.qualifiedName;
            this.displayName = json.displayName;
            this.allowChildren = json.allowChildren;
            this.final = json.final;
            this.abstract = json.abstract;
            this.superType = json.superType;
            this.contentDisplayNameScript = json.contentDisplayNameScript;
            this.iconUrl = json.iconUrl;
            this.createdTime = new Date(json.createdTime);
            this.modifiedTime = new Date(json.modifiedTime);
            this.owner = json.owner;
            this.modifier = json.modifier;
        }

        getName():string {
            return this.name;
        }

        getQualifiedName():string {
            return this.qualifiedName;
        }

        getDisplayName():string {
            return this.displayName;
        }

        isFinal():boolean {
            return this.final;
        }

        isAbstract():boolean {
            return this.abstract;
        }

        isAllowChildren():boolean {
            return this.allowChildren;
        }

        getSuperType():string {
            return this.superType;
        }

        getContentDisplayNameScript():string {
            return this.contentDisplayNameScript;
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

        getIconUrl():string {
            return this.iconUrl;
        }

    }
}