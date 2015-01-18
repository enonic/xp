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

            var toolbar = new api.dom.DivEl('toolbar');
            container.appendChild(toolbar);

            var crop = new Button().addClass("icon-crop2");
            var rotateLeft = new Button().addClass("icon-rotate");
            var rotateRight = new Button().addClass("icon-rotate2");
            var flipHorizontal = new Button().addClass("icon-flip");
            var flipVertical = new Button().addClass("icon-flip2");
            var palette = new Button().addClass("icon-palette");

            toolbar.appendChildren(crop, rotateLeft, rotateRight, flipHorizontal, flipVertical, palette);

            return container;
        }

    }
}