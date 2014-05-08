module api.content.page.image {

    export class ImageUploadedEvent extends api.event.Event2 {

        private uploadedItem: api.ui.UploadItem;

        constructor(uploadedItem: api.ui.UploadItem) {
            super('imageUploadedEvent.liveEdit');
            this.uploadedItem = uploadedItem;
        }

        getUploadedItem(): api.ui.UploadItem {
            return this.uploadedItem;
        }

        static on(handler: (event: ImageUploadedEvent) => void, contextWindow: Window = window) {
            api.event.onEvent2('imageUploadedEvent.liveEdit', handler, contextWindow);
        }

        static un(handler: (event: ImageUploadedEvent) => void, contextWindow: Window = window) {
            api.event.unEvent2('imageUploadedEvent.liveEdit', handler, contextWindow);
        }
    }
}