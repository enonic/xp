module api.schema.relationshiptype {

    export class RelationshipTypeResourceRequest<T> extends api.rest.ResourceRequest<T> {

        private resourceUrl:api.rest.Path;

        constructor() {
            super();
            this.resourceUrl = api.rest.Path.fromParent(super.getRestPath(), "schema/relationship");
        }

        getResourcePath():api.rest.Path {
            return this.resourceUrl;
        }

        fromJsonToReleationshipType(json:api.schema.relationshiptype.json.RelationshipTypeJson): api.schema.relationshiptype.RelationshipType {
            return new api.schema.relationshiptype.RelationshipType(json);
        }
    }
}