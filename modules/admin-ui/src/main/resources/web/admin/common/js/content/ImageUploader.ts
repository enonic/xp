module api.content {

    import Button = api.ui.button.Button;
    import CloseButton = api.ui.button.CloseButton;

    import Point = api.ui.image.Point;
    import Rect = api.ui.image.Rect;
    import ImageEditor = api.ui.image.ImageEditor;

    export interface ImageUploaderConfig extends MediaUploaderConfig {
        scaleWidth: boolean;
    }

    export class ImageUploader extends MediaUploader {

        private images: api.dom.ImgEl[];
        private imageEditors: ImageEditor[];
        private focalEditModeListeners: {(edit: boolean, position: Point): void}[];
        private cropEditModeListeners: {(edit: boolean, crop: Rect): void}[];

        private initialWidth: number;

        private scaleWidth: boolean = false; // parameter states if width of the image must be preferred over its height during resolving

        constructor(config: ImageUploaderConfig) {
            super(config);

            this.images = [];
            this.imageEditors = [];
            this.focalEditModeListeners = [];
            this.cropEditModeListeners = [];

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

            var imageEditor = new ImageEditor(imgUrl);
            imageEditor.onFocusModeChanged((edit: boolean, position: Point) => {
                this.setResetVisible(!edit);
                this.notifyFocalPointEditModeChanged(edit, position);
            });
            imageEditor.onCropModeChanged((edit, crop) => {
                this.setResetVisible(!edit);
                this.notifyCropEditModeChanged(edit, crop);
            });
            var image = imageEditor.getImage();

            image.getEl().setWidthPx(this.initialWidth);
            this.getEl().setMaxWidthPx(this.initialWidth);

            image.onLoaded(() => {
                this.getEl().setMaxWidthPx(image.getEl().getNaturalWidth());
            });
            image.onRemoved(() => {
                this.getEl().setMaxWidthPx(this.initialWidth);
            });

            this.onFocus(() => {
                setTimeout(() => {
                    if (!imageEditor.hasClass('selected')) {
                        this.toggleSelected(imageEditor);
                    }
                }, 150);
            });

            this.onBlur((event) => {
                if (imageEditor.hasClass('selected') && !api.ObjectHelper.objectEquals(event.relatedTarget, this.getResetButton())) {
                    this.toggleSelected(imageEditor);
                }
            });

            image.onClicked((event: MouseEvent) => {
                this.toggleSelected(imageEditor);

                event.stopPropagation();
                event.preventDefault();
            });

            this.imageEditors.push(imageEditor);

            api.dom.Body.get().onClicked((event: MouseEvent) => {
                this.imageEditors.forEach((editor) => {
                    if (editor.hasClass('selected')) {
                        editor.removeClass('selected');
                        if (wemjq(this.getHTMLElement()).has(editor.getHTMLElement()).length) {
                            this.setResetVisible(false);
                        }
                    }
                });
            });

            return imageEditor;
        }

        private toggleSelected(imageEditor: ImageEditor) {
            imageEditor.toggleClass('selected');
            this.setResetVisible(imageEditor.hasClass('selected'));
        }

        setFocalPoint(x: number, y: number) {
            this.imageEditors.forEach((editor: ImageEditor) => {
                editor.setFocusPosition(x, y);
            })
        }

        setCrop(x: number, y: number, w: number, h: number) {
            this.imageEditors.forEach((editor: ImageEditor) => {
                editor.setCropPosition(x, y, w, h);
            })
        }

        isFocalPointEditMode(): boolean {
            return this.imageEditors.some((editor: ImageEditor) => {
                return editor.isFocusEditMode();
            });
        }

        isCropEditMode(): boolean {
            return this.imageEditors.some((editor: ImageEditor) => {
                return editor.isCropEditMode();
            })
        }

        onCropEditModeChanged(listener: (edit: boolean, crop: Rect) => void) {
            this.cropEditModeListeners.push(listener);
        }

        unCropEditModeChanged(listener: (edit: boolean, crop: Rect) => void) {
            this.cropEditModeListeners = this.cropEditModeListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyCropEditModeChanged(edit: boolean, crop: Rect) {
            this.cropEditModeListeners.forEach((listener) => {
                listener(edit, crop);
            })
        }

        onFocalPointEditModeChanged(listener: (edit: boolean, position: Point) => void) {
            this.focalEditModeListeners.push(listener);
        }

        unFocalPointEditModeChanged(listener: (edit: boolean, position: Point) => void) {
            this.focalEditModeListeners = this.focalEditModeListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyFocalPointEditModeChanged(edit: boolean, position: Point) {
            this.focalEditModeListeners.forEach((listener) => {
                listener(edit, position);
            })
        }

    }
}