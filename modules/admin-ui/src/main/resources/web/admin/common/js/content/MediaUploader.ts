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

            this.addClass('media-uploader');
            this.propertyIdProvider = api.Client.get().getPropertyIdProvider();
        }


        createModel(serverResponse: api.content.json.ContentJson): Content {
            if (serverResponse) {
                return new api.content.ContentBuilder().
                    fromContentJson(<api.content.json.ContentJson> serverResponse, this.propertyIdProvider).
                    build();
            }
            else {
                return null;
            }
        }

        getModelValue(item: Content): string {
            return item.getId();
        }

        createResultItem(value: string): api.dom.Element {

            var link = new api.dom.AEl().setUrl(api.util.UriHelper.getRestUri('content/media/' + value), "_blank");
            link.setHtml(value);

            return link;
        }
    }
}