module api_schema_mixin {

    export class MixinIconUrlResolver extends api_schema.SchemaIconUrlResolver {

        public resolveDefault(): string {
            return this.toRestUrl(api_rest.Path.fromParent(this.getResourcePath(), "Mixin:_"));
        }
    }
}