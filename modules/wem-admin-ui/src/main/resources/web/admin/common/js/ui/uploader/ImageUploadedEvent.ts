module api.ui.uploader {

    export class ImageUploadedEvent {

        private uploadedItem: UploadItem;

        constructor(uploadItem: UploadItem) {
            this.uploadedItem = uploadItem;
        }

        getUploadedItem(): UploadItem {
            return this.uploadedItem;
        }
    }
}