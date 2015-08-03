module api.schema.mixin {

    import ApplicationKey = api.application.ApplicationKey;

    export class GetMixinsByModuleRequest extends MixinResourceRequest<MixinListJson, Mixin[]> {

        private applicationKey: ApplicationKey;

        constructor(applicationKey: ApplicationKey) {
            super();
            super.setMethod("GET");
            this.applicationKey = applicationKey;
        }

        getParams(): Object {
            return {
                applicationKey: this.applicationKey.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "byModule");
        }

        sendAndParse(): wemQ.Promise<Mixin[]> {

            return this.send().then((response: api.rest.JsonResponse<MixinListJson>) => {
                return response.getResult().mixins.map((mixinJson: MixinJson) => {
                    return this.fromJsonToMixin(mixinJson);
                })
            });
        }
    }
}
