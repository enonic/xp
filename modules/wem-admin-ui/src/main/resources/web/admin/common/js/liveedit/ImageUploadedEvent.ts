module api.liveedit {

    import Event2 = api.event.Event2;
    import ImagePlaceholder = api.liveedit.image.ImagePlaceholder;

    export class ImageUploadedEvent extends Event2 {

        private uploadedItem: api.ui.UploadItem;

        private targetImagePlaceholder: ImagePlaceholder;

        constructor(uploadedItem: api.ui.UploadItem, target: ImagePlaceholder) {
            super();
            this.uploadedItem = uploadedItem;
            this.targetImagePlaceholder = target;
        }

        getUploadedItem(): api.ui.UploadItem {
            return this.uploadedItem;
        }

        getTargetImagePlaceholder(): ImagePlaceholder {
            return this.targetImagePlaceholder;
        }

        static on(handler: (event: ImageUploadedEvent) => void, contextWindow: Window = window) {
            Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: ImageUploadedEvent) => void, contextWindow: Window = window) {
            Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}