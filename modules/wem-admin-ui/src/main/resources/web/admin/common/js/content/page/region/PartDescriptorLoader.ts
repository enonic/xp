module api.content.page.region {

    export class PartDescriptorLoader extends api.util.loader.BaseLoader<PartDescriptorsJson, PartDescriptor> {

        constructor(request: PartDescriptorsResourceRequest) {
            super(request);
        }

        filterFn(descriptor: PartDescriptor) {
            return descriptor.getDisplayName().toString().toLowerCase().indexOf(this.getSearchString().toLowerCase()) != -1;
        }


    }
}