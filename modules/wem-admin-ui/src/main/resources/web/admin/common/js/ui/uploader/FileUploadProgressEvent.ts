module api.ui.uploader {

    export class FileUploadProgressEvent<ITEM> {

        private uploadItem: ITEM;

        private progress: number;

        constructor(uploadItem: ITEM, progress: number) {
            this.uploadItem = uploadItem;
            this.progress = progress;
        }

        getUploadItem(): ITEM {
            return this.uploadItem;
        }

        getProgress(): number {
            return this.progress;
        }
    }
}