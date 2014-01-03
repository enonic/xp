module api.schema.mixin {

    export class MixinIconUrlResolver extends api.schema.SchemaIconUrlResolver {

        public resolveDefault(): string {
            return this.toRestUrl(api.rest.Path.fromParent(this.getResourcePath(), "Mixin:_"));
        }
    }
}