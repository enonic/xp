module api.content {

    import Button = api.ui.button.Button;
    import CloseButton = api.ui.button.CloseButton;

    export interface ImageUploaderConfig extends MediaUploaderConfig {
    }

    export class ImageUploader extends MediaUploader {

        private images: api.dom.ImgEl[];

        constructor(config: ImageUploaderConfig) {
            this.images = [];

            if (config.allowTypes == undefined) {
                config.allowTypes = [
                    {title: 'Image files', extensions: 'jpg,gif,png'}
                ];
            }

            super(config);
            this.addClass('image-uploader');
        }

        createResultItem(value: string): api.dom.DivEl {
            var container = new api.dom.DivEl();

            var imgUrl = new ContentImageUrlResolver().
                setContentId(new api.content.ContentId(value)).
                setSize(this.getEl().getWidth()).
                setTimestamp(new Date()).
                resolve();

            var image = new api.dom.ImgEl(imgUrl);

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