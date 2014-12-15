module api.content {

    import Button = api.ui.button.Button;
    import CloseButton = api.ui.button.CloseButton;

    export interface ContentUploaderConfig extends api.ui.uploader.UploaderConfig {
    }

    export class ContentUploader extends api.ui.uploader.Uploader<Content> {

        private propertyIdProvider: api.data.PropertyIdProvider;

        constructor(config: ContentUploaderConfig) {

            if (config.url == undefined) {
                config.url = api.util.UriHelper.getRestUri("content/createMedia")
            }

            super(config);

            this.propertyIdProvider = api.Client.get().getPropertyIdProvider();
        }

        createUploadItem(file): Content {

            var builder = new ContentBuilder().
                setData(new api.data.PropertyTree()).
                setName(ContentName.fromString(file.id)).
                setDisplayName(file.name);
            return (<ContentBuilder> builder).build();
        }

        updateUploadItem(item: Content, serverResponse): Content {
            if (serverResponse) {
                return item =
                       item.newBuilder().fromContentJson(<api.content.json.ContentJson> serverResponse, this.propertyIdProvider).build();
            }
        }

        getUploadItemId(item: Content): string {
            return item.getName().toString();
        }

        getUploadItemValue(item: Content): string {
            return item.getPath().toString();
        }
    }
}