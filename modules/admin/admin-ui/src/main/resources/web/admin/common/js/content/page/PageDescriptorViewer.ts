module api.content.page {

    export class PageDescriptorViewer extends api.ui.NamesAndIconViewer<PageDescriptor> {

        constructor() {
            super();
        }

        resolveDisplayName(object: PageDescriptor): string {
            return object.getDisplayName();
        }

        resolveSubName(object: PageDescriptor, relativePath: boolean = false): string {
            return object.getKey().toString();
        }

        resolveIconClass(object: PageDescriptor): string {
            return api.StyleHelper.getCommonIconCls("file") + " icon-large";
        }
    }

}
