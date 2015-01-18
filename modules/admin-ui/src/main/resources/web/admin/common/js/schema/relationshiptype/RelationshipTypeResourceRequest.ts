module api.schema.relationshiptype {

    export class RelationshipTypeResourceRequest<JSON_TYPE, PARSED_TYPE> extends api.rest.ResourceRequest<JSON_TYPE, PARSED_TYPE> {

        private resourceUrl: api.rest.Path;

        constructor() {
            super();
            this.resourceUrl = api.rest.Path.fromParent(super.getRestPath(), "schema/relationship");
        }

        getResourcePath(): api.rest.Path {
            return this.resourceUrl;
        }

        fromJsonToReleationshipType(json: api.schema.relationshiptype.RelationshipTypeJson): RelationshipType {
            return RelationshipType.fromJson(json);
        }
    }
}