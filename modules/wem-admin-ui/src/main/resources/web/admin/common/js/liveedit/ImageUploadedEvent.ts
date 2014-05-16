module api.liveedit {

    import Event2 = api.event.Event2;

    export class ImageUploadedEvent extends Event2 {

        private uploadedItem: api.ui.UploadItem;

        constructor(uploadedItem: api.ui.UploadItem) {
            super();
            this.uploadedItem = uploadedItem;
        }

        getUploadedItem(): api.ui.UploadItem {
            return this.uploadedItem;
        }

        static on(handler: (event: ImageUploadedEvent) => void, contextWindow: Window = window) {
            Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: ImageUploadedEvent) => void, contextWindow: Window = window) {
            Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}