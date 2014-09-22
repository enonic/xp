module api.schema.mixin {

    export class GetMixinByQualifiedNameRequest extends MixinResourceRequest<api.schema.mixin.MixinJson, Mixin> {

        private name: MixinName;

        constructor(name: MixinName) {
            super();
            super.setMethod("GET");
            this.name = name;
        }

        getParams(): Object {
            return {
                name: this.name.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return super.getResourcePath();
        }

        sendAndParse(): wemQ.Promise<Mixin> {

            return this.send().then((response: api.rest.JsonResponse<api.schema.mixin.MixinJson>) => {
                return this.fromJsonToMixin(response.getResult());
            });
        }
    }
}