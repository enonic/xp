module api.ui.uploader {

    export class FileUploadFailedEvent<ITEM extends api.Equitable> {

        private uploadItem: UploadItem<ITEM>;

        constructor(uploadItem: UploadItem<ITEM>) {
            this.uploadItem = uploadItem;
        }

        getUploadItem(): UploadItem<ITEM> {
            return this.uploadItem;
        }
    }
}