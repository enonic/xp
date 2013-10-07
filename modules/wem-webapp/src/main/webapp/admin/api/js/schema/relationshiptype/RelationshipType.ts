module api_schema_relationshiptype {

    export class RelationshipType {

        private name:string;
        private displayName:string;
        private createdTime:Date;
        private modifiedTime:Date;
        private qualifiedName:string;
        private fromSemantic:string;
        private toSemantic:string;
        private allowedFromTypes:string[];
        private allowedToTypes:string[];
        private icon:string;

        constructor(relationshipTypeJson:api_schema_relationshiptype_json.RelationshipTypeJson) {
            this.name = relationshipTypeJson.name;
            this.displayName = relationshipTypeJson.displayName;
            this.fromSemantic = relationshipTypeJson.fromSemantic;
            this.toSemantic = relationshipTypeJson.toSemantic;
            this.allowedFromTypes = relationshipTypeJson.allowedFromTypes;
            this.allowedToTypes = relationshipTypeJson.allowedToTypes;
            this.icon = relationshipTypeJson.iconUrl;
            this.modifiedTime = new Date(relationshipTypeJson.modifiedTime);
            this.createdTime = new Date(relationshipTypeJson.createdTime);
        }

        getName():string {
            return this.name;
        }

        getDisplayName():string {
            return this.displayName;
        }

        getCreatedTime():Date {
            return this.createdTime;
        }

        getModifiedTime():Date {
            return this.modifiedTime;
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