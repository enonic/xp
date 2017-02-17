module api.content.page {

    export class GetPageDescriptorsByApplicationRequest extends PageDescriptorResourceRequest<PageDescriptorsJson, PageDescriptor[]> {

        private applicationKey: api.application.ApplicationKey;

        constructor(applicationKey: api.application.ApplicationKey) {
            super();
            super.setMethod('GET');
            this.applicationKey = applicationKey;
        }

        getParams(): Object {
            return {
                applicationKey: this.applicationKey.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'list', 'by_application');
        }

        sendAndParse(): wemQ.Promise<PageDescriptor[]> {

            if (!api.BrowserHelper.isIE()) { // In case frame was reloaded in IE it can't use objects from cache
                const cached = this.cache.getByApplication(this.applicationKey); // as they are from old unreachable for IE frame
                if (cached) {
                    return wemQ(cached);
                }
            }

            return this.send().then((response: api.rest.JsonResponse<PageDescriptorsJson>) => {
                return this.fromJsonToPageDescriptors(response.getResult());
            });
        }
    }
}
