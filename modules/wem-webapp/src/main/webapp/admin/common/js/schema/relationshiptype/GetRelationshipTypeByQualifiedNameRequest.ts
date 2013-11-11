module api_schema_relationshiptype {

    export class GetRelationshipTypeByQualifiedNameRequest extends RelationshipTypeResourceRequest<api_schema_relationshiptype_json.RelationshipTypeJson> {

        private qualifiedName:string;

        constructor(qualifiedName:string) {
            super();
            super.setMethod("GET");
            this.qualifiedName = qualifiedName;
        }

        getParams():Object {
            return {
                qualifiedName: this.qualifiedName
            };
        }

        getRequestPath():api_rest.Path {
            return super.getResourcePath();
        }
    }
}