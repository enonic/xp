module api.content {

    import Button = api.ui.button.Button;
    import CloseButton = api.ui.button.CloseButton;

    export interface ImageUploaderConfig extends ContentUploaderConfig {
    }

    export class ImageUploader extends ContentUploader {

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

        setValue(value: string): ImageUploader {
            super.setValue(value);

            var results = this.getResultContainer();
            results.removeChildren();
            this.images.length = 0;

            this.parseValues(value).forEach((val) => {
                if (val) {
                    results.appendChild(this.createImageResult(val));
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

        private createImageResult(value: string): api.dom.DivEl {
            var container = new api.dom.DivEl();

            var src: string;
            if (value && (value.indexOf('/') == -1)) {
                src = api.util.UriHelper.getRestUri('blob/' + value + '?mimeType=image/png');
            } else {
                src = value;
            }

            var image = new api.dom.ImgEl(src);

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

            toolbar.appendChildren([crop, rotateLeft, rotateRight, flipHorizontal, flipVertical, palette]);

            return container;
        }

    }
}