module api_schema_relationshiptype {

    export class RelationshipTypeIconUrlResolver extends api_schema.SchemaIconUrlResolver {

        public resolveDefault(): string {
            return api_rest.Path.fromParent(this.getResourcePath(),  "RelationshipType:_").toString();
        }
    }
}