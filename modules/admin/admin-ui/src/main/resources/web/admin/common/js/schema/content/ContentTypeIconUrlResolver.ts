module api.schema.content {

    export class ContentTypeIconUrlResolver extends api.schema.SchemaIconUrlResolver {

        static default(): string {
            return api.util.UriHelper.getRestUri(api.rest.Path.fromParent(api.schema.SchemaIconUrlResolver.getResourcePath(),
                "base:structured").toString());
        }
    }
}