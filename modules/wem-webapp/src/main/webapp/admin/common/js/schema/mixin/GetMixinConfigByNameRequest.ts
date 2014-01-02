module api.schema.mixin {

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

        getRequestPath():api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "config");
        }
    }
}