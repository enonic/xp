module api_schema_relationshiptype {

    export class RelationshipTypeResourceRequest<T> extends api_rest.ResourceRequest<T> {

        private resourceUrl:api_rest.Path;

        constructor() {
            super();
            this.resourceUrl = api_rest.Path.fromParent(super.getRestPath(), "schema/relationship");
        }

        getResourcePath():api_rest.Path {
            return this.resourceUrl;
        }

        fromJsonToReleationshipType(json:api_schema_relationshiptype_json.RelationshipTypeJson): api_schema_relationshiptype.RelationshipType {
            return new api_schema_relationshiptype.RelationshipType(json);
        }
    }
}