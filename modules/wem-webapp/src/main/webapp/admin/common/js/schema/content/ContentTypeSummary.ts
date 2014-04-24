module api.schema.content{

    export class ContentTypeSummary extends api.schema.Schema {

        private allowChildContent:boolean;

        private abstract:boolean;

        private final:boolean;

        private superType:api.schema.content.ContentTypeName;

        private contentDisplayNameScript:string;

        private modifier:string;

        private owner:string;

        constructor(json:api.schema.content.json.ContentTypeSummaryJson) {
            super(json);
            this.allowChildContent = json.allowChildContent;
            this.final = json.final;
            this.abstract = json.abstract;
            this.superType = new api.schema.content.ContentTypeName(json.superType);
            this.contentDisplayNameScript = json.contentDisplayNameScript;
            this.owner = json.owner;
            this.modifier = json.modifier;
        }

        getContentTypeName(): api.schema.content.ContentTypeName {
            return new api.schema.content.ContentTypeName(this.getName());
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

        getSuperType():api.schema.content.ContentTypeName {
            return this.superType;
        }

        hasContentDisplayNameScript(): boolean {
            return !api.util.isStringBlank(this.contentDisplayNameScript);
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

        static fromJsonArray(jsonArray:api.schema.content.json.ContentTypeSummaryJson[]):ContentTypeSummary[] {
            var array:ContentTypeSummary[] = [];

            jsonArray.forEach( (summaryJson:api.schema.content.json.ContentTypeSummaryJson) => {
                array.push(new ContentTypeSummary(summaryJson));
            } );
            return array;
        }

    }
}