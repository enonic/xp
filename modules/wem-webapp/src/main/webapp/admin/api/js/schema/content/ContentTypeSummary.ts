module api_schema_content{

    export class ContentTypeSummary extends api_schema.Schema {

        private allowChildContent:boolean;

        private abstract:boolean;

        private final:boolean;

        private superType:string;

        private contentDisplayNameScript:string;

        private modifier:string;

        private owner:string;


        constructor(json:api_schema_content_json.ContentTypeSummaryJson) {
            super(json);
            this.allowChildContent = json.allowChildContent;
            this.final = json.final;
            this.abstract = json.abstract;
            this.superType = json.superType;
            this.contentDisplayNameScript = json.contentDisplayNameScript;
            this.owner = json.owner;
            this.modifier = json.modifier;
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

    }
}