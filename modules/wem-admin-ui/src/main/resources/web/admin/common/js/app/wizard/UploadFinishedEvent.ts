module api.app.wizard {

    export class UploadFinishedEvent {

        private uploadItem: api.ui.uploader.UploadItem<any>;

        constructor(uploadItem: api.ui.uploader.UploadItem<any>) {
            this.uploadItem = uploadItem;
        }

        getUploadItem(): api.ui.uploader.UploadItem<any> {
            return this.uploadItem;
        }
    }
}