module api_schema_relationshiptype {

    export class GetRelationshipTypeConfigByNameRequest extends RelationshipTypeResourceRequest<GetRelationshipTypeConfigResult> {

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
            return api_rest.Path.fromParent(super.getResourcePath(), "config");
        }
    }
}