module api.content.page {

    import ApplicationKey = api.application.ApplicationKey;

    export class PageDescriptorLoader extends api.util.loader.BaseLoader<PageDescriptorsJson, PageDescriptor> {

        protected request: GetPageDescriptorsByApplicationsRequest;

        constructor() {
            super();

            this.setComparator(new api.content.page.DescriptorByDisplayNameComparator());
        }

        filterFn(descriptor: PageDescriptor) {
            return descriptor.getDisplayName().toString().toLowerCase().indexOf(this.getSearchString().toLowerCase()) != -1;
        }

        protected createRequest(): GetPageDescriptorsByApplicationsRequest {
            return new GetPageDescriptorsByApplicationsRequest();
        }

        setApplicationKeys(applicationKeys: ApplicationKey[]) {
            this.request.setApplicationKeys(applicationKeys);
        }


    }
}