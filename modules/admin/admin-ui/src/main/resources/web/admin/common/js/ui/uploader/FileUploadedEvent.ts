module api.ui.uploader {

    export class FileUploadedEvent<ITEM extends api.Equitable> {

        private uploadItem: UploadItem<ITEM>;

        constructor(uploadItem: UploadItem<ITEM>) {
            this.uploadItem = uploadItem;
        }

        getUploadItem(): UploadItem<ITEM> {
            return this.uploadItem;
        }
    }
}