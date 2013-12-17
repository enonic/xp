module api_schema_mixin {

    export class GetMixinByQualifiedNameRequest extends MixinResourceRequest<api_schema_mixin_json.MixinJson> {

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
            return super.getResourcePath();
        }

        sendAndParse(): JQueryPromise<api_schema_mixin.Mixin> {

            var deferred = jQuery.Deferred<api_schema_mixin.Mixin>();

            this.send().done((response: api_rest.JsonResponse<api_schema_mixin_json.MixinJson>) => {
                deferred.resolve(this.fromJsonToMixin(response.getResult()));
            }).fail((response: api_rest.RequestError) => {
                    deferred.reject(null);
                });

            return deferred;
        }
    }
}