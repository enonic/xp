module api.content.page.part {

    export class PartDescriptorLoader extends api.util.loader.BaseLoader<json.PartDescriptorsJson, PartDescriptor> {

        constructor(request: PartDescriptorsResourceRequest) {
            super(request);
        }

        filterFn(descriptor: PartDescriptor) {
            return descriptor.getDisplayName().toString().indexOf(this.getSearchString().toLowerCase()) != -1;
        }


    }
}