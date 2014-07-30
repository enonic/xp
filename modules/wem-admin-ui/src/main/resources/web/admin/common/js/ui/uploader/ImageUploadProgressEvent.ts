module api.ui.uploader {

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