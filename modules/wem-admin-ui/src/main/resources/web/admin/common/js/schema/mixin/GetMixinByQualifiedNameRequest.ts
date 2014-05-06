module api.schema.mixin {

    export class GetMixinByQualifiedNameRequest extends MixinResourceRequest<api.schema.mixin.json.MixinJson> {

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
            return super.getResourcePath();
        }

        sendAndParse(): Q.Promise<api.schema.mixin.Mixin> {

            return this.send().then((response: api.rest.JsonResponse<api.schema.mixin.json.MixinJson>) => {
                return this.fromJsonToMixin(response.getResult());
            });
        }
    }
}