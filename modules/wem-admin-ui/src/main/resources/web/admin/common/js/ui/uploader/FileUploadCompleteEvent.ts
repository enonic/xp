module api.ui.uploader {

    export class FileUploadCompleteEvent<ITEM> {

        private uploadedItems: ITEM[];

        constructor(uploadItems: ITEM[]) {
            this.uploadedItems = uploadItems;
        }

        getUploadedItems(): ITEM[] {
            return this.uploadedItems;
        }
    }
}