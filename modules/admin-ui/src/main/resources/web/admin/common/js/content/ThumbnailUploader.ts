module api.content {

    import Button = api.ui.button.Button;
    import CloseButton = api.ui.button.CloseButton;

    export interface ThumbnailUploaderConfig extends api.ui.uploader.UploaderConfig {

    }

    export class ThumbnailUploader extends api.ui.uploader.Uploader<Content> {

        private iconUrlResolver: ContentIconUrlResolver;

        constructor(config?: ThumbnailUploaderConfig) {

            if (config.url == undefined) {
                config.url = api.util.UriHelper.getRestUri("content/updateThumbnail");
            }
            if (config.showCancel == undefined) {
                config.showCancel = false;
            }
            if (config.showReset == undefined) {
                config.showReset = false;
            }
            if (config.dropzoneAlwaysVisible == undefined) {
                config.dropzoneAlwaysVisible = true;
            }
            if (config.resultAlwaysVisisble == undefined) {
                config.resultAlwaysVisisble = true;
            }
            if (config.allowTypes == undefined) {
                config.allowTypes = [
                    {title: 'Image files', extensions: 'jpg,gif,png'}
                ];
            }
            if (config.allowMultiSelection == undefined) {
                config.allowMultiSelection = false;
            }

            super(config);

            this.addClass('thumbnail-uploader');
            this.iconUrlResolver = new ContentIconUrlResolver();
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
            return this.iconUrlResolver.setContent(item).resolve();
        }

        createResultItem(value: string): api.dom.Element {
            return new api.dom.ImgEl(value);
        }

    }
}