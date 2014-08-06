module api.schema {

    import Schema = api.schema.Schema;
    import SchemaJson = api.schema.SchemaJson;
    import SchemaListJson = api.schema.SchemaListJson;

    export class SchemaTreeRequest extends SchemaResourceRequest<SchemaListJson> {

        private parentId: string;

        constructor(parentId: string) {
            super();
            super.setMethod("GET");
            this.parentId = parentId;
        }

        getParams(): Object {
            return {
                parentId: this.parentId
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "list");
        }

        sendAndParse(): Q.Promise<Schema[]> {

            return this.send().then((response: api.rest.JsonResponse<SchemaListJson>) => {
                return this.fromJsonArrayToSchemaArray(response.getResult().schemas);
            });
        }

        private fromJsonArrayToSchemaArray(jsonArray: SchemaJson[]): Schema[] {

            var summaryArray: Schema[] = [];
            jsonArray.forEach((schemaJson: SchemaJson) => {
                var schema: Schema = this.schemaFromJson(schemaJson);
                summaryArray.push(schema);
            });
            return summaryArray;
        }

        private schemaFromJson(schemaJson: SchemaJson): Schema {
            var kind = SchemaKind.fromString(schemaJson.schemaKind);

            switch (kind) {
                case SchemaKind.MIXIN:
                    return api.schema.mixin.Mixin.fromJson(<api.schema.mixin.json.MixinJson> schemaJson);
                case SchemaKind.CONTENT_TYPE:
                    return api.schema.content.ContentTypeSummary.fromJson(<api.schema.content.json.ContentTypeJson> schemaJson);
                case SchemaKind.RELATIONSHIP_TYPE:
                    return api.schema.relationshiptype.RelationshipType.fromJson(<api.schema.relationshiptype.json.RelationshipTypeJson> schemaJson);
            }

        }
    }
}