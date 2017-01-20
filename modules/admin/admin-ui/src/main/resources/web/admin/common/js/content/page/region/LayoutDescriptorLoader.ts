module api.content.page.region {

    export class LayoutDescriptorLoader extends api.util.loader.BaseLoader<LayoutDescriptorsJson, LayoutDescriptor> {

        protected request: GetLayoutDescriptorsByApplicationsRequest;

        constructor() {
            super();

            this.setComparator(new api.content.page.DescriptorByDisplayNameComparator());
        }

        filterFn(descriptor: LayoutDescriptor) {
            return descriptor.getDisplayName().toString().toLowerCase().indexOf(this.getSearchString().toLowerCase()) !== -1;
        }

        protected createRequest(): GetLayoutDescriptorsByApplicationsRequest {
            return new GetLayoutDescriptorsByApplicationsRequest();
        }

        setApplicationKeys(applicationKeys: ApplicationKey[]) {
            this.request.setApplicationKeys(applicationKeys);
        }
    }
}
