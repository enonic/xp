module api.content.page.part {

    export class GetPartDescriptorsByModulesRequest extends PartDescriptorsResourceRequest {

        private moduleKeys: api.module.ModuleKey[];

        constructor(moduleKeys: api.module.ModuleKey[]) {
            super();
            super.setMethod("POST");
            this.moduleKeys = moduleKeys;
        }

        getParams(): Object {
            return {
                moduleKeys: api.module.ModuleKey.toStringArray(this.moduleKeys)
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "list", "by_modules");
        }
    }
}