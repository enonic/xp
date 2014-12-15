module api.ui.uploader {

    import Button = api.ui.button.Button;
    import CloseButton = api.ui.button.CloseButton;

    export interface FileUploaderConfig extends UploaderConfig {
    }

    export class FileUploader extends Uploader<UploadItem> {

        constructor(config: FileUploaderConfig) {

            if (config.url == undefined) {
                config.url = api.util.UriHelper.getRestUri("blob/upload");
            }
            super(config);
        }

        createUploadItem(file): UploadItem {
            return new UploadItemBuilder().
                setId(file.id).
                setName(file.name).
                setMimeType(file.type).
                setProgress(file.loaded).
                setSize(file.size).
                build();
        }

        updateUploadItem(item: UploadItem, serverResponse): UploadItem {
            if (serverResponse.items && serverResponse.items.length > 0) {
                var uploadedItem = serverResponse.items[0];
                return item.
                    setBlobKey(new api.blob.BlobKey(uploadedItem.id)).
                    setMimeType(uploadedItem.mimeType).
                    setProgress(100);
            }
        }

        getUploadItemId(item: UploadItem): string {
            return item.getId();
        }

        getUploadItemValue(item: UploadItem): string {
            return item.getBlobKey().toString();
        }
    }
}