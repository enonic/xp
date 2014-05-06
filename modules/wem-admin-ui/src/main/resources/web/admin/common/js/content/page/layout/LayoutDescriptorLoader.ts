module api.content.page.layout {

    export class LayoutDescriptorLoader extends api.util.loader.BaseLoader<LayoutDescriptorsJson, LayoutDescriptor> {

        constructor(request: LayoutDescriptorsResourceRequest) {
            super(request);
        }

        filterFn(descriptor:LayoutDescriptor)
        {
            return descriptor.getDisplayName().toString().toLowerCase().indexOf(this.getSearchString().toLowerCase()) != -1;
        }

    }
}