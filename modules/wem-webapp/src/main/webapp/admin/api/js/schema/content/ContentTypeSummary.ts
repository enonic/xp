module api_schema_content{

    export class ContentTypeSummary extends api_item.Item {

        private name:string;

        private qualifiedName:string;

        private displayName:string;

        private allowChildContent:boolean;

        private abstract:boolean;

        private final:boolean;

        private superType:string;

        private contentDisplayNameScript:string;

        private iconUrl:string;

        private modifier:string;

        private owner:string;


        constructor(json:api_schema_content_json.ContentTypeSummaryJson) {
            super(json);
            this.name = json.name;
            this.qualifiedName = json.qualifiedName;
            this.displayName = json.displayName;
            this.allowChildContent = json.allowChildContent;
            this.final = json.final;
            this.abstract = json.abstract;
            this.superType = json.superType;
            this.contentDisplayNameScript = json.contentDisplayNameScript;
            this.iconUrl = json.iconUrl;
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

        isAllowChildContent():boolean {
            return this.allowChildContent;
        }

        getSuperType():string {
            return this.superType;
        }

        getContentDisplayNameScript():string {
            return this.contentDisplayNameScript;
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