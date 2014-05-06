module api.schema.relationshiptype {

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

        getRequestPath():api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "config");
        }
    }
}