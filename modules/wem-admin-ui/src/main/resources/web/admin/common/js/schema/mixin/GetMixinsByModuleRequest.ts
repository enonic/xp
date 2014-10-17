module api.schema.mixin {

    import ModuleKey = api.module.ModuleKey;

    export class GetMixinsByModuleRequest extends MixinResourceRequest<MixinListJson, Mixin[]> {

        private moduleKey: ModuleKey;

        constructor(moduleKey: ModuleKey) {
            super();
            super.setMethod("GET");
            this.moduleKey = moduleKey;
        }

        getParams(): Object {
            return {
                moduleKey: this.moduleKey.toString()
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
