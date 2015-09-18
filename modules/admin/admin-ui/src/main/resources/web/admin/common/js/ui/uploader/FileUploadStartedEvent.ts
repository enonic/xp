module api.ui.uploader {

    export class FileUploadStartedEvent<ITEM> {

        private uploadItems: UploadItem<ITEM>[];

        constructor(uploadItems: UploadItem<ITEM>[]) {
            this.uploadItems = uploadItems;
        }

        getUploadItems(): UploadItem<ITEM>[] {
            return this.uploadItems;
        }
    }
}