module api.ui {

    export class ImageUploadProgressEvent {

        private uploadItem: UploadItem;

        constructor(uploadItem: UploadItem) {
            this.uploadItem = uploadItem;
        }

        getUploadItem(): UploadItem {
            return this.uploadItem;
        }
    }
}