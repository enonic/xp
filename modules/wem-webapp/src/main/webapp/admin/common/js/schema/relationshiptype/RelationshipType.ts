module api.schema.relationshiptype {

    export class RelationshipType extends api.schema.Schema {

        private fromSemantic:string;

        private toSemantic:string;

        private allowedFromTypes:string[];

        private allowedToTypes:string[];

        constructor(relationshipTypeJson:api.schema.relationshiptype.json.RelationshipTypeJson) {
            super(relationshipTypeJson);
            this.fromSemantic = relationshipTypeJson.fromSemantic;
            this.toSemantic = relationshipTypeJson.toSemantic;
            this.allowedFromTypes = relationshipTypeJson.allowedFromTypes;
            this.allowedToTypes = relationshipTypeJson.allowedToTypes;
        }

        getRelationshiptypeName():RelationshipTypeName {
            return new RelationshipTypeName(this.getName());
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

    }
}