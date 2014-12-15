module api.ui.uploader {

    export class FileUploadStartedEvent<ITEM> {

        private uploadedItems: ITEM[];

        constructor(uploadItems: ITEM[]) {
            this.uploadedItems = uploadItems;
        }

        getUploadItems(): ITEM[] {
            return this.uploadedItems;
        }
    }
}