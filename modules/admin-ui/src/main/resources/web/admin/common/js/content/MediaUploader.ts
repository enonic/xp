module api.content {

    import Button = api.ui.button.Button;
    import CloseButton = api.ui.button.CloseButton;
    import ValueTypes = api.data.ValueTypes;

    export enum MediaUploaderOperation
    {
        create,
        update
    }

    export interface MediaUploaderConfig extends api.ui.uploader.UploaderConfig {

        operation: MediaUploaderOperation;
    }

    export class MediaUploader extends api.ui.uploader.Uploader<Content> {

        private fileName: string;

        constructor(config: MediaUploaderConfig) {

            if (config.url == undefined) {
                config.url = api.util.UriHelper.getRestUri("content/" + MediaUploaderOperation[config.operation] + "Media")
            }

            super(config);

            this.addClass('media-uploader');
        }


        createModel(serverResponse: api.content.json.ContentJson): Content {
            if (serverResponse) {
                return new api.content.ContentBuilder().
                    fromContentJson(<api.content.json.ContentJson> serverResponse).
                    build();
            }
            else {
                return null;
            }
        }

        getModelValue(item: Content): string {
            return item.getId();
        }

        getMediaValue(item: Content): api.data.Value {
            var mediaProperty = item.getContentData().getProperty("media");
            var mediaValue;
            switch (mediaProperty.getType()) {
            case ValueTypes.DATA:
                mediaValue = mediaProperty.getPropertySet().getProperty('attachment').getValue();
                break;
            case ValueTypes.STRING:
                mediaValue = mediaProperty.getValue();
                break;
            }
            return mediaValue;
        }

        setFileName(name: string) {
            this.fileName = name;
        }

        createResultItem(value: string): api.dom.Element {

            var link = new api.dom.AEl().setUrl(api.util.UriHelper.getRestUri('content/media/' + value), "_blank");
            link.setHtml(this.fileName != null && this.fileName != "" ? this.fileName : value);

            return link;
        }
    }
}