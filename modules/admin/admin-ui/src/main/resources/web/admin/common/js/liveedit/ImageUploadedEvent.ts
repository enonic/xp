module api.liveedit {

    import Event = api.event.Event;
    import Content = api.content.Content;
    import ImagePlaceholder = api.liveedit.image.ImagePlaceholder;

    export class ImageUploadedEvent extends api.event.Event {

        private uploadedItem: Content;

        private targetImagePlaceholder: ImagePlaceholder;

        constructor(uploadedItem: Content, target: ImagePlaceholder) {
            super();
            this.uploadedItem = uploadedItem;
            this.targetImagePlaceholder = target;
        }

        getUploadedItem(): Content {
            return this.uploadedItem;
        }

        getTargetImagePlaceholder(): ImagePlaceholder {
            return this.targetImagePlaceholder;
        }

        static on(handler: (event: ImageUploadedEvent) => void, contextWindow: Window = window) {
            Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: ImageUploadedEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}