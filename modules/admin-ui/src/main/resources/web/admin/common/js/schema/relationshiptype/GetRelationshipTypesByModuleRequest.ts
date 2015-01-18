module api.schema.relationshiptype {

    import ModuleKey = api.module.ModuleKey;

    export class GetRelationshipTypesByModuleRequest extends RelationshipTypeResourceRequest<RelationshipTypeListJson, RelationshipType[]> {

        private moduleKey: ModuleKey;

        constructor(moduleKey: ModuleKey) {
            super();
            super.setMethod("GET");
            this.moduleKey = moduleKey;
        }

        getParams(): Object {
            return {
                moduleKey: this.moduleKey.toString()
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