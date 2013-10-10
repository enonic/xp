module api_schema_relationshiptype {

    export class RelationshipTypeResourceRequest extends api_rest.ResourceRequest {

        private resourceUrl:api_rest.Path;

        constructor() {
            super();
            this.resourceUrl = api_rest.Path.fromParent(super.getRestPath(), "schema/relationship");
        }

        getResourcePath():api_rest.Path {
            return this.resourceUrl;
        }
    }
}