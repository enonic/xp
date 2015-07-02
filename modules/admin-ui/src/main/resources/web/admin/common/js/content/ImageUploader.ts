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
        private cropEditModeListeners: {(edit: boolean, crop: Rect, zoom: Rect): void}[];

        private initialWidth: number;
        private originalHeight: number;
        private originalWidth: number;

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

        setOriginalDimensions(width: string, height: string) {
            this.originalWidth = parseInt(width);
            this.originalHeight = parseInt(height);
        }

        private getProportionalHeight(): number {
            return Math.round(this.initialWidth * this.originalHeight / this.originalWidth);
        }

        createResultItem(value: string): api.dom.DivEl {
            this.initialWidth = this.getParentElement().getEl().getWidth();

            this.getResultContainer().getEl().setHeightPx(this.getProportionalHeight());
            this.getResultContainer().getEl().addClass("placeholder");
            this.setResetVisible(false);

            var imgUrl = new ContentImageUrlResolver().
                setContentId(new api.content.ContentId(value)).
                setTimestamp(new Date()).
                resolve();

            var imageEditor = new ImageEditor(imgUrl);
            imageEditor.onFocusModeChanged((edit: boolean, position: Point) => {
                this.setResetVisible(!edit);
                this.notifyFocalPointEditModeChanged(edit, position);
            });
            imageEditor.onCropModeChanged((edit, crop, zoom) => {
                    imageEditor.removeClass('selected');
                    this.notifyCropEditModeChanged(edit, crop, zoom);
            });

            imageEditor.getImage().onLoaded((event: UIEvent) => {
                this.getResultContainer().getEl().removeClass("placeholder");
            });

            this.onFocus(() => {
                setTimeout(() => {
                    if (!imageEditor.hasClass('selected')) {
                        this.toggleSelected(imageEditor);
                    }
                }, 150);
            });

            imageEditor.getLastButtonInContainer().onBlur(() => {
                this.toggleSelected(imageEditor);
            });

            this.onBlur((event: FocusEvent) => {
                if (event.relatedTarget && !imageEditor.isElementInsideButtonsContainer(<HTMLElement>event.relatedTarget)) {
                    this.toggleSelected(imageEditor);
                }
            });

            this.onClicked((event: MouseEvent) => {
                if (event.target && !imageEditor.isElementInsideButtonsContainer(<HTMLElement>event.target)) {
                    this.toggleSelected(imageEditor);
                }
            });

            this.imageEditors.push(imageEditor);

            api.dom.Body.get().onClicked((event: MouseEvent) => {
                this.imageEditors.forEach((editor) => {
                    if (editor.hasClass('selected') && imageEditor.getImage().getHTMLElement() !== event.target) {
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

        setCrop(crop: Rect) {
            this.imageEditors.forEach((editor: ImageEditor) => {
                editor.setCropPosition(crop.x, crop.y, crop.w, crop.h);
            })
        }

        setZoom(zoom: Rect) {
            this.imageEditors.forEach((editor: ImageEditor) => {
                editor.setZoomPosition(zoom.x, zoom.y, zoom.w, zoom.h);
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

        onCropEditModeChanged(listener: (edit: boolean, crop: Rect, zoom: Rect) => void) {
            this.cropEditModeListeners.push(listener);
        }

        unCropEditModeChanged(listener: (edit: boolean, crop: Rect, zoom: Rect) => void) {
            this.cropEditModeListeners = this.cropEditModeListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyCropEditModeChanged(edit: boolean, crop: Rect, zoom: Rect) {
            this.cropEditModeListeners.forEach((listener) => {
                listener(edit, crop, zoom);
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