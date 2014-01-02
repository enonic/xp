module api.schema.content {

    export class ContentTypeIconUrlResolver extends api.schema.SchemaIconUrlResolver {

        public resolveDefault(): string {
            return this.toRestUrl(api.rest.Path.fromParent(this.getResourcePath(), "ContentType:structured"));
        }
    }
}