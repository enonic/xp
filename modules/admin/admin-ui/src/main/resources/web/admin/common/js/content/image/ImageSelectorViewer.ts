module api.content.image {

    export class ImageSelectorViewer extends api.ui.NamesAndIconViewer<ImageSelectorDisplayValue> {

        constructor() {
            super();
        }

        resolveDisplayName(object: ImageSelectorDisplayValue): string {
            return object.getDisplayName();
        }

        resolveUnnamedDisplayName(object: ImageSelectorDisplayValue): string {
            return object.getTypeLocaleName();
        }

        resolveSubName(object: ImageSelectorDisplayValue, relativePath: boolean = false): string {
            return object.getPath() ? object.getPath().toString() : '';
        }

        resolveIconUrl(object: ImageSelectorDisplayValue): string {
            return object.getImageUrl() + '?crop=false';
        }
    }
}
