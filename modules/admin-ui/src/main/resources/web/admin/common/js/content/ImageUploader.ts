module api.content {

    import Button = api.ui.button.Button;
    import CloseButton = api.ui.button.CloseButton;

    export interface ImageUploaderConfig extends MediaUploaderConfig {
        scaleWidth: boolean;
    }

    export class ImageUploader extends MediaUploader {

        private images: api.dom.ImgEl[];
        private imageEditors: api.ui.image.ImageEditor[];
        private focalEditModeListeners: {(edit: boolean, position: {x: number; y: number}): void}[];

        private initialWidth: number;

        private scaleWidth: boolean = false; // parameter states if width of the image must be preferred over its height during resolving

        constructor(config: ImageUploaderConfig) {
            super(config);

            this.images = [];
            this.imageEditors = [];
            this.focalEditModeListeners = [];

            if (config.scaleWidth != undefined) {
                this.scaleWidth = config.scaleWidth;
            }

            if (config.allowTypes == undefined) {
                config.allowTypes = [
                    {title: 'Image files', extensions: 'jpg,gif,png'}
                ];
            }

            this.addClass('image-uploader');

            this.initialWidth = 0;
            this.onShown(() => {
                if (this.getEl().getWidth() == 0) {
                    this.initialWidth = Math.max(this.getParentElement().getEl().getWidth(), this.initialWidth);
                    this.getEl().setMaxWidthPx(this.initialWidth);
                }
            });
        }

        createResultItem(value: string): api.dom.DivEl {
            this.initialWidth = this.getParentElement().getEl().getWidth();

            var imgUrl = new ContentImageUrlResolver().
                setContentId(new api.content.ContentId(value)).
                setSize(this.initialWidth).
                setTimestamp(new Date()).
                setScaleWidth(this.scaleWidth).
                resolve();

            var imageEditor = new api.ui.image.ImageEditor(imgUrl);
            imageEditor.onFocusModeChanged((edit, position) => {
                this.setResetVisible(!edit);
                this.notifyFocalPointEditModeChanged(edit, position);
            });
            imageEditor.onCropModeChanged((edit, crop) => {
                this.setResetVisible(!edit);
            });
            var image = imageEditor.getImage();

            this.getEl().setMaxWidthPx(image.getEl().getNaturalWidth());

            image.onLoaded(() => {
                this.getEl().setMaxWidthPx(image.getEl().getNaturalWidth());
            });
            image.onRemoved(() => {
                this.getEl().setMaxWidthPx(this.initialWidth);
            });

            imageEditor.onClicked((event: MouseEvent) => {
                imageEditor.toggleClass('selected');

                event.stopPropagation();
                event.preventDefault();
            });

            this.imageEditors.push(imageEditor);

            api.dom.Body.get().onClicked((event: MouseEvent) => {
                this.imageEditors.forEach((editor) => {
                    editor.removeClass('selected');
                });
            });

            return imageEditor;
        }

        setFocalPoint(x: number, y: number) {
            this.imageEditors.forEach((editor: api.ui.image.ImageEditor) => {
                editor.setFocusPosition(x, y);
            })
        }

        isFocalPointEditMode(): boolean {
            return this.imageEditors.some((editor: api.ui.image.ImageEditor) => {
                return editor.isFocusEditMode();
            });
        }

        onFocalPointEditModeChanged(listener: (edit: boolean, position: {x: number; y: number}) => void) {
            this.focalEditModeListeners.push(listener);
        }

        unFocalPointEditModeChanged(listener: (edit: boolean, position: {x: number; y: number}) => void) {
            this.focalEditModeListeners = this.focalEditModeListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyFocalPointEditModeChanged(edit: boolean, position: {x: number; y: number}) {
            this.focalEditModeListeners.forEach((listener) => {
                listener(edit, position);
            })
        }

    }
}