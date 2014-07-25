module api.app.wizard {

    export class UploadFinishedEvent {

        private uploadItem: api.ui.uploader.UploadItem;

        constructor(uploadItem: api.ui.uploader.UploadItem) {
            this.uploadItem = uploadItem;
        }

        getUploadItem(): api.ui.uploader.UploadItem {
            return this.uploadItem;
        }
    }
}