module api_schema_content {

    export class ContentTypeIconUrlResolver extends api_schema.SchemaIconUrlResolver {

        public resolveDefault(): string {
            return api_rest.Path.fromParent(this.getResourcePath(),  "ContentType:structured").toString();
        }
    }
}