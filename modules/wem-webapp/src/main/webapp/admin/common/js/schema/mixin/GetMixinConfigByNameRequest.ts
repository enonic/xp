module api_schema_mixin {

    export class GetMixinConfigByNameRequest extends MixinResourceRequest<GetMixinConfigResult> {

        private name:MixinName;

        constructor(name:MixinName) {
            super();
            super.setMethod("GET");
            this.name = name;
        }

        getParams():Object {
            return {
                name: this.name.toString()
            };
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "config");
        }
    }
}