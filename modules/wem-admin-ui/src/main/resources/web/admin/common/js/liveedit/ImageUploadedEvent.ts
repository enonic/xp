module api.liveedit {

    import Event = api.event.Event;
    import ImagePlaceholder = api.liveedit.image.ImagePlaceholder;

    export class ImageUploadedEvent extends api.event.Event {

        private uploadedItem: api.ui.uploader.UploadItem;

        private targetImagePlaceholder: ImagePlaceholder;

        constructor(uploadedItem: api.ui.uploader.UploadItem, target: ImagePlaceholder) {
            super();
            this.uploadedItem = uploadedItem;
            this.targetImagePlaceholder = target;
        }

        getUploadedItem(): api.ui.uploader.UploadItem {
            return this.uploadedItem;
        }

        getTargetImagePlaceholder(): ImagePlaceholder {
            return this.targetImagePlaceholder;
        }

        static on(handler: (event: ImageUploadedEvent) => void, contextWindow: Window = window) {
            Event.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: ImageUploadedEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}