module api_schema_relationshiptype {

    export class GetRelationshipTypeByNameRequest extends RelationshipTypeResourceRequest<api_schema_relationshiptype_json.RelationshipTypeJson> {

        private name:RelationshipTypeName;

        constructor(name:RelationshipTypeName) {
            super();
            super.setMethod("GET");
            this.name = name;
        }

        getParams():Object {
            return {
                name: this.name.toString()
            };
        }

        getRequestPath():api_rest.Path {
            return super.getResourcePath();
        }
    }
}