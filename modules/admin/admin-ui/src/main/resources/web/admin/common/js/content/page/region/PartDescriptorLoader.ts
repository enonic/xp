module api.content.page.region {

    import ApplicationKey = api.application.ApplicationKey;

    export class PartDescriptorLoader extends api.util.loader.BaseLoader<PartDescriptorsJson, PartDescriptor> {

        protected request: GetPartDescriptorsByApplicationsRequest;

        constructor() {
            super();

            this.setComparator(new api.content.page.DescriptorByDisplayNameComparator());
        }

        filterFn(descriptor: PartDescriptor) {
            return descriptor.getDisplayName().toString().toLowerCase().indexOf(this.getSearchString().toLowerCase()) != -1;
        }

        protected createRequest(): GetPartDescriptorsByApplicationsRequest {
            return new GetPartDescriptorsByApplicationsRequest();
        }

        setApplicationKeys(applicationKeys: ApplicationKey[]) {
            this.request.setApplicationKeys(applicationKeys);
        }
    

    }
}