module api.content {

    import Button = api.ui.button.Button;
    import CloseButton = api.ui.button.CloseButton;

    export interface ImageUploaderConfig extends MediaUploaderConfig {
        skipWizardEvents: boolean;
    }

    export class ImageUploader extends MediaUploader {

        private images: api.dom.ImgEl[];

        private initialWidth: number;

        constructor(config: ImageUploaderConfig) {
            this.images = [];

            if (config.allowTypes == undefined) {
                config.allowTypes = [
                    {title: 'Image files', extensions: 'jpg,gif,png'}
                ];
            }

            super(config);
            this.addClass('image-uploader');

            this.initialWidth = 0;
            this.onShown(() => {
                if(this.getEl().getWidth() == 0) {
                    this.initialWidth = Math.max(this.getParentElement().getEl().getWidth(), this.initialWidth);
                    this.getEl().setMaxWidthPx(this.initialWidth);
                }
            });

            if (!config.skipWizardEvents) {
                this.onFileUploaded((event: api.ui.uploader.FileUploadedEvent<api.content.Content>) => {
                    new api.app.wizard.ContentWizardImageUploadedEvent(event.getUploadItem().getModel(), this).fire();
                });
            }
        }

        createResultItem(value: string): api.dom.DivEl {
            this.initialWidth = this.getParentElement().getEl().getWidth();
            var container = new api.dom.DivEl();

            var imgUrl = new ContentImageUrlResolver().
                setContentId(new api.content.ContentId(value)).
                setSize(this.initialWidth).
                setTimestamp(new Date()).
                resolve();

            var image = new api.dom.ImgEl(imgUrl);
            this.getEl().setMaxWidthPx(image.getEl().getNaturalWidth());

            image.onLoaded(() => {
                this.getEl().setMaxWidthPx(image.getEl().getNaturalWidth());
            });
            image.onRemoved(() => {
                this.getEl().setMaxWidthPx(this.initialWidth);
            });

            image.onClicked((event: MouseEvent) => {
                image.toggleClass('selected');

                event.stopPropagation();
                event.preventDefault();
            });

            container.appendChild(image);
            this.images.push(image);

            api.dom.Body.get().onClicked((event: MouseEvent) => {
                this.images.forEach((img) => {
                    img.removeClass('selected');
                });
            });

            return container;
        }

    }
}