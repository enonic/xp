module api.app.wizard {

    export class UploadFinishedEvent {

        private uploadItem: api.ui.UploadItem;

        constructor(uploadItem: api.ui.UploadItem) {
            this.uploadItem = uploadItem;
        }

        getUploadItem(): api.ui.UploadItem {
            return this.uploadItem;
        }
    }
}