module api_schema_relationshiptype {

    export class RelationshipType extends api_item.BaseItem{

        private name:string;

        private displayName:string;

        private qualifiedName:string;

        private fromSemantic:string;

        private toSemantic:string;

        private allowedFromTypes:string[];

        private allowedToTypes:string[];

        private icon:string;

        constructor(relationshipTypeJson:api_schema_relationshiptype_json.RelationshipTypeJson) {
            super(relationshipTypeJson);
            this.name = relationshipTypeJson.name;
            this.displayName = relationshipTypeJson.displayName;
            this.fromSemantic = relationshipTypeJson.fromSemantic;
            this.toSemantic = relationshipTypeJson.toSemantic;
            this.allowedFromTypes = relationshipTypeJson.allowedFromTypes;
            this.allowedToTypes = relationshipTypeJson.allowedToTypes;
            this.icon = relationshipTypeJson.iconUrl;
        }

        getName():string {
            return this.name;
        }

        getDisplayName():string {
            return this.displayName;
        }

        getQualifiedName():string {
            return this.qualifiedName;
        }

        getFromSemantic():string {
            return this.fromSemantic;
        }

        getToSemantic():string {
            return this.toSemantic;
        }

        getAllowedFromTypes():string[] {
            return this.allowedFromTypes;
        }

        getAllowedToTypes():string[] {
            return this.allowedToTypes;
        }

        getIcon():string {
            return this.icon;
        }
    }
}