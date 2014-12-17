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

                var uploadedItem: UploadItem = serverResponse.items[0];

                return UploadItem.create(item).
                    setBlobKey(new api.blob.BlobKey(uploadedItem.getId())).
                    setMimeType(uploadedItem.getMimeType()).
                    setProgress(100).
                    build();
            }
            return null;
        }

        getUploadItemId(item: UploadItem): string {
            return item.getId();
        }

        getUploadItemValue(item: UploadItem): string {
            return item.getBlobKey().toString();
        }

        setValue(value: string): FileUploader {
            super.setValue(value);

            var results = this.getResultContainer();
            results.removeChildren();

            this.parseValues(value).forEach((val) => {
                if (val) {
                    results.appendChild(this.createBinaryResult(val));
                }
            });

            return this;
        }

        private parseValues(jsonString: string): string[] {
            try {
                var o = JSON.parse(jsonString);

                // Handle non-exception-throwing cases:
                // Neither JSON.parse(false) or JSON.parse(1234) throw errors, hence the type-checking,
                // but... JSON.parse(null) returns 'null', and typeof null === "object",
                if (o && typeof o === "object" && o.length) {
                    return o;
                }
            } catch (e) { }

            // Value is not JSON so just return it
            return [jsonString];
        }

        private createBinaryResult(value: string): api.dom.AEl {
            var url;
            if (value && (value.indexOf('/') == -1)) {
                url = api.util.UriHelper.getRestUri('blob/' + value + '?mimeType=application/octet-stream');
            } else {
                url = value;
            }
            var link = new api.dom.AEl().setUrl(url, "_blank");
            link.setHtml(value);

            return link;
        }
    }
}