module api.schema.mixin {

    export class MixinResourceRequest<T> extends api.rest.ResourceRequest<T> {
        private resourceUrl: api.rest.Path;

        constructor() {
            super();
            this.resourceUrl = api.rest.Path.fromParent(super.getRestPath(), "schema/mixin");
        }

        getResourcePath(): api.rest.Path {
            return this.resourceUrl;
        }

        fromJsonToMixin(json: api.schema.mixin.json.MixinJson) {
            return Mixin.fromJson(json);
        }
    }
}