module api.ui.uploader {

    export class ImageUploadStartedEvent {

        private uploadedItems: UploadItem[];

        constructor(uploadItems: UploadItem[]) {
            this.uploadedItems = uploadItems;
        }

        getUploadedItems(): UploadItem[] {
            return this.uploadedItems;
        }
    }
}