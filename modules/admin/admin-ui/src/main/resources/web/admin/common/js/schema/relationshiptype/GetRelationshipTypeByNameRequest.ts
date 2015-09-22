module api.schema.relationshiptype {

    export class GetRelationshipTypeByNameRequest extends RelationshipTypeResourceRequest<RelationshipTypeJson, RelationshipType> {

        private name: RelationshipTypeName;

        constructor(name: RelationshipTypeName) {
            super();
            super.setMethod("GET");
            this.name = name;
        }

        getParams(): Object {
            return {
                name: this.name.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return super.getResourcePath();
        }

        sendAndParse(): wemQ.Promise<RelationshipType> {

            return this.send().then((response: api.rest.JsonResponse<RelationshipTypeJson>) => {
                return this.fromJsonToReleationshipType(response.getResult());
            });
        }
    }
}