module api.ui {

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