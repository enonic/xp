module api.content.page.region {

    export class PartDescriptorViewer extends api.ui.NamesAndIconViewer<PartDescriptor> {

        constructor() {
            super();
        }

        resolveDisplayName(object: PartDescriptor): string {
            return object.getDisplayName();
        }

        resolveSubName(object: PartDescriptor, relativePath: boolean = false): string {
            return object.getKey().toString();
        }

        resolveIconClass(object: PartDescriptor): string {
            return "icon-puzzle icon-large";
        }
    }
}