module api.ui.uploader {

    import Button = api.ui.button.Button;
    import CloseButton = api.ui.button.CloseButton;
    import ValueTypes = api.data.ValueTypes;

    export enum MediaUploaderElOperation {
        create,
        update
    }

    export interface MediaUploaderElConfig extends api.ui.uploader.UploaderElConfig {

        operation: MediaUploaderElOperation;
    }

    export class MediaUploaderEl extends api.ui.uploader.UploaderEl<api.content.Content> {

        private fileName: string;

        private link: api.dom.AEl;

        constructor(config: MediaUploaderElConfig) {

            if (config.url == undefined) {
                config.url = api.util.UriHelper.getRestUri("content/" + MediaUploaderElOperation[config.operation] + "Media");
            }

            super(config);

            this.addClass('media-uploader-el');
        }

        createModel(serverResponse: api.content.json.ContentJson): api.content.Content {
            if (serverResponse) {
                return new api.content.ContentBuilder().
                fromContentJson(<api.content.json.ContentJson> serverResponse).
                build();
            } else {
                return null;
            }
        }

        getModelValue(item: api.content.Content): string {
            return item.getId();
        }

        getMediaValue(item: api.content.Content): api.data.Value {
            let mediaProperty = item.getContentData().getProperty("media");
            let mediaValue;
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
            if (this.link && this.fileName != null && this.fileName != "") {
                this.link.setHtml(this.fileName);
            }
        }

        createResultItem(value: string): api.dom.Element {
            this.link = new api.dom.AEl().setUrl(api.util.UriHelper.getRestUri('content/media/' + value), "_blank");
            this.link.setHtml(this.fileName != null && this.fileName != "" ? this.fileName : value);

            return this.link;
        }
    }
}
