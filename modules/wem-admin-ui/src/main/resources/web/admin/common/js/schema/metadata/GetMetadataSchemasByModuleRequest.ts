module api.schema.metadata {

    import ModuleKey = api.module.ModuleKey;

    export class GetMetadataSchemasByModuleRequest extends api.rest.ResourceRequest<MetadataSchemaListJson, MetadataSchema[]> {

        private moduleKey: ModuleKey;

        constructor(moduleKey: ModuleKey) {
            super();
            super.setMethod("GET");
            this.moduleKey = moduleKey;
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getRestPath(), "schema/metadata/byModule");
        }

        getParams(): Object {
            return {
                moduleKey: this.moduleKey.toString()
            };
        }

        sendAndParse(): wemQ.Promise<MetadataSchema[]> {
            return this.send().then((response: api.rest.JsonResponse<MetadataSchemaListJson>) => {
                return response.getResult().metadataSchemas.map((json: MetadataSchemaJson) => {
                    return MetadataSchema.fromJson(json);
                });
            });
        }

    }

}