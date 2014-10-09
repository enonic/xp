module api.schema.metadata {

    export class GetMetadataSchemaRequest extends api.rest.ResourceRequest<MetadataSchemaJson, MetadataSchema> {

        private name: MetadataSchemaName;

        constructor(name: MetadataSchemaName) {
            super();
            super.setMethod("GET");
            this.name = name;
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getRestPath(), "schema/metadata");
        }

        getParams(): Object {
            return {
                name: this.name
            };
        }

        fromJsonToMetadataSchema(json: MetadataSchemaJson): MetadataSchema {
            return MetadataSchema.fromJson(json);
        }

        sendAndParse(): wemQ.Promise<MetadataSchema> {
            return this.send().then((response: api.rest.JsonResponse<MetadataSchemaJson>) => {
                return this.fromJsonToMetadataSchema(response.getResult());
            });
        }

    }

}