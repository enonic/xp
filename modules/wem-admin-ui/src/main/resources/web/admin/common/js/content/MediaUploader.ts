module api.content {

    import Button = api.ui.button.Button;
    import CloseButton = api.ui.button.CloseButton;

    export enum MediaUploaderOperation
    {
        create,
        update
    }

    export interface MediaUploaderConfig extends api.ui.uploader.UploaderConfig {

        operation: MediaUploaderOperation;
    }

    export class MediaUploader extends api.ui.uploader.Uploader<Content> {

        private propertyIdProvider: api.data.PropertyIdProvider;

        constructor(config: MediaUploaderConfig) {

            if (config.url == undefined) {
                config.url = api.util.UriHelper.getRestUri("content/" + MediaUploaderOperation[config.operation] + "Media")
            }

            super(config);

            this.propertyIdProvider = api.Client.get().getPropertyIdProvider();
        }

        createUploadItem(file): Content {
            var builder = new ContentBuilder().
                setData(new api.data.PropertyTree(this.propertyIdProvider)).
                setId(file.id).
                setDisplayName(file.name);
            return (<ContentBuilder> builder).build();
        }

        updateUploadItem(item: Content, serverResponse): Content {
            if (serverResponse) {
                // is this the best way to update content ?
                item = item.newBuilder().fromContentJson(<api.content.json.ContentJson> serverResponse, this.propertyIdProvider).build();
                return item;
            }
        }

        getUploadItemId(item: Content): string {
            return item.getId();
        }

        getUploadItemValue(item: Content): string {
            return item.getId();
        }
    }
}