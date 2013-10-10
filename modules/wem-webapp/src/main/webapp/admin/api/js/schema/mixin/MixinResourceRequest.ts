module api_schema_mixin {

    export class MixinResourceRequest extends api_rest.ResourceRequest {
        private resourceUrl:api_rest.Path;

        constructor() {
            super();
            this.resourceUrl = api_rest.Path.fromParent(super.getRestPath(), "schema/mixin");
        }

        getResourcePath():api_rest.Path {
            return this.resourceUrl;
        }
    }
}