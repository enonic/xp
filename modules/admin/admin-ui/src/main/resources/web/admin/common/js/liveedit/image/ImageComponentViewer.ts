module api.liveedit.image {

    export class ImageComponentViewer extends api.ui.NamesAndIconViewer<api.content.page.region.ImageComponent> {

        constructor() {
            super();
        }

        resolveDisplayName(object: api.content.page.region.ImageComponent): string {
            return !!object.getName() ? object.getName().toString() : "";
        }

        resolveSubName(object: api.content.page.region.ImageComponent, relativePath: boolean = false): string {
            return object.getPath().toString();
        }

        resolveIconClass(object: api.content.page.region.ImageComponent): string {
            return "live-edit-font-icon-image";
        }

    }

}
