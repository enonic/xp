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

        setValue(value: string): MediaUploader {
            super.setValue(value);

            var results = this.getResultContainer();
            results.removeChildren();

            this.parseValues(value).forEach((val) => {
                if (val) {
                    results.appendChild(this.createResultItem(val));
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

        createResultItem(value: string): api.dom.Element {

            var link = new api.dom.AEl().setUrl(api.util.UriHelper.getRestUri('content/media/' + value), "_blank");
            link.setHtml(value);

            return link;
        }
    }
}