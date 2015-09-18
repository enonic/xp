module api.ui.uploader {

    export class FileUploadCompleteEvent<ITEM> {

        private uploadItems: UploadItem<ITEM>[];

        constructor(uploadItems: UploadItem<ITEM>[]) {
            this.uploadItems = uploadItems;
        }

        getUploadItems(): UploadItem<ITEM>[] {
            return this.uploadItems;
        }
    }
}