module api.schema.relationshiptype {

    export class RelationshipTypeIconUrlResolver extends api.schema.SchemaIconUrlResolver {

        public resolveDefault(): string {
            return this.toRestUrl(api.rest.Path.fromParent(this.getResourcePath(), "RelationshipType:_"));
        }
    }
}