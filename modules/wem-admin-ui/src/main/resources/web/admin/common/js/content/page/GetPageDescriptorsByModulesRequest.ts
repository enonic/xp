module api.content.page {

    export class GetPageDescriptorsByModulesRequest extends PageDescriptorResourceRequest<PageDescriptorsJson, PageDescriptor[]> {

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

        sendAndParse(): wemQ.Promise<PageDescriptor[]> {

            return this.send().then((response: api.rest.JsonResponse<PageDescriptorsJson>) => {
                return this.fromJsonToPageDescriptors(response.getResult());
            });
        }

        private fromJsonToPageDescriptors(json: PageDescriptorsJson): PageDescriptor[] {

            var array: api.content.page.PageDescriptor[] = [];
            json.descriptors.forEach((descriptorJson: PageDescriptorJson)=> {
                array.push(this.fromJsonToPageDescriptor(descriptorJson));
            });
            return array;
        }

    }
}