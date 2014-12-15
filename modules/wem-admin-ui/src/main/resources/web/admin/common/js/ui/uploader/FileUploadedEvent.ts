module api.ui.uploader {

    export class FileUploadedEvent<ITEM> {

        private uploadedItem: ITEM;

        constructor(uploadItem: ITEM) {
            this.uploadedItem = uploadItem;
        }

        getUploadItem(): ITEM {
            return this.uploadedItem;
        }
    }
}