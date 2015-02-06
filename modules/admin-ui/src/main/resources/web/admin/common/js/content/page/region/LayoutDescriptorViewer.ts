module api.content.page.region {

    export class LayoutDescriptorViewer extends api.ui.NamesAndIconViewer<LayoutDescriptor> {

        constructor() {
            super();
        }

        resolveDisplayName(object: LayoutDescriptor): string {
            return object.getDisplayName();
        }

        resolveSubName(object: LayoutDescriptor, relativePath: boolean = false): string {
            return object.getKey().toString();
        }

        resolveIconClass(object: LayoutDescriptor): string {
            return "icon-insert-template icon-large";
        }
    }
}