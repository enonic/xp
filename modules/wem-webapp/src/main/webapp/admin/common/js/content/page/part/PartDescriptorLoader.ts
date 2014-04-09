module api.content.page.part {

    export class PartDescriptorLoader extends api.util.loader.BaseLoader<PartDescriptorsJson, PartDescriptor> {

        constructor(request: PartDescriptorsResourceRequest) {
            super(request);
        }

        filterFn(descriptor: PartDescriptor) {
            console.log("filtering part descriptors", arguments);
            return descriptor.getDisplayName().toString().toLowerCase().indexOf(this.getSearchString().toLowerCase()) != -1;
        }


    }
}