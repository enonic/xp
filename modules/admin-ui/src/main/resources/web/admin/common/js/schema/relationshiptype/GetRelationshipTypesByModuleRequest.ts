module api.schema.relationshiptype {

    import ApplicationKey = api.application.ApplicationKey;

    export class GetRelationshipTypesByModuleRequest extends RelationshipTypeResourceRequest<RelationshipTypeListJson, RelationshipType[]> {

        private applicationKey: ApplicationKey;

        constructor(applicationKey: ApplicationKey) {
            super();
            super.setMethod("GET");
            this.applicationKey = applicationKey;
        }

        getParams(): Object {
            return {
                applicationKey: this.applicationKey.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "byModule");
        }

        sendAndParse(): wemQ.Promise<RelationshipType[]> {

            return this.send().then((response: api.rest.JsonResponse<RelationshipTypeListJson>) => {
                return response.getResult().relationshipTypes.map((json: RelationshipTypeJson) => this.fromJsonToReleationshipType(json));
            });
        }
    }
}