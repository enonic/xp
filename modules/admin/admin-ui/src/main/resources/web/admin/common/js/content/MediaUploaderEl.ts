module api.content {

    import Button = api.ui.button.Button;
    import CloseButton = api.ui.button.CloseButton;
    import ValueTypes = api.data.ValueTypes;

    export enum MediaUploaderElOperation
    {
        create,
        update
    }

    export interface MediaUploaderElConfig extends api.ui.uploader.UploaderElConfig {

        operation: MediaUploaderElOperation;
    }

    export class MediaUploaderEl extends api.ui.uploader.UploaderEl<Content> {

        //TODO: should be extended from FileUploaderEl
        protected contentId: string;

        constructor(config: MediaUploaderElConfig) {

            if (config.url == undefined) {
                config.url = api.util.UriHelper.getRestUri("content/" + MediaUploaderElOperation[config.operation] + "Media")
            }

            super(config);

            this.addClass('media-uploader-el');
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
            return item.getDisplayName().toString();
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

        setContentId(contentId: string) {
            this.contentId = contentId;
        }

        createResultItem(value: string): api.dom.Element {
            var link = new api.dom.AEl().setUrl(api.util.UriHelper.getRestUri('content/media/' + this.contentId), "_blank");
            link.setHtml(value);

            return link;
        }
    }
}