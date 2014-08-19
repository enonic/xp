module api.schema.relationshiptype {

    export class RelationshipTypeIconUrlResolver extends api.schema.SchemaIconUrlResolver {

        static default(): string {
            return api.util.getRestUri(api.rest.Path.fromParent(api.schema.SchemaIconUrlResolver.getResourcePath(), "Mixin:_").toString());
        }
    }
}