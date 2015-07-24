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

        private imageEditors: ImageEditor[];
        private focusEditModeListeners: {(edit: boolean, position: Point): void}[];
        private focusAutoPositionedListeners: {(auto: boolean): void}[];
        private cropEditModeListeners: {(edit: boolean, crop: Rect, zoom: Rect): void}[];
        private cropAutoPositionedListeners: {(auto: boolean): void}[];

        private initialWidth: number;
        private originalHeight: number;
        private originalWidth: number;

        private scaleWidth: boolean; // parameter states if width of the image must be preferred over its height during resolving

        constructor(config: ImageUploaderConfig) {
            if (config.allowTypes == undefined) {
                config.allowTypes = [
                    {title: 'Image files', extensions: 'jpg,jpeg,gif,png'}
                ];
            }

            super(config);

            this.scaleWidth = false;
            this.imageEditors = [];
            this.focusEditModeListeners = [];
            this.focusAutoPositionedListeners = [];
            this.cropEditModeListeners = [];
            this.cropAutoPositionedListeners = [];

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
                this.setResetVisible(false);

                if (this.getEl().getWidth() == 0) {
                    this.initialWidth = Math.max(this.getParentElement().getEl().getWidth(), this.initialWidth);
                    this.getEl().setMaxWidthPx(this.initialWidth);
                }
            });

            this.onUploadStarted(() => {
                this.imageEditors.forEach((imageEditor: ImageEditor) => {
                    imageEditor.remove();
                });
                this.imageEditors = [];
            });

            this.onFocus(() => {
                setTimeout(() => {
                    if (this.imageEditors.length && !this.imageEditors[0].hasClass('selected')) {
                        this.toggleSelected(this.imageEditors[0]);
                    }
                }, 150);
            });

            this.onBlur((event: FocusEvent) => {
                this.imageEditors.forEach((imageEditor: ImageEditor) => {
                    if (event.relatedTarget && !imageEditor.isElementInsideButtonsContainer(<HTMLElement>event.relatedTarget)) {
                        this.toggleSelected(imageEditor);
                    }
                });
            });

            this.onClicked((event: MouseEvent) => {
                this.imageEditors.forEach((imageEditor: ImageEditor) => {
                    if (event.target && !imageEditor.isElementInsideButtonsContainer(<HTMLElement>event.target)) {
                        this.toggleSelected(imageEditor);
                    }
                });
            });

            api.dom.Body.get().onClicked((event: MouseEvent) => {
                this.imageEditors.forEach((imageEditor: ImageEditor) => {
                    if (imageEditor.hasClass('selected') && imageEditor.getImage().getHTMLElement() !== event.target) {
                        imageEditor.removeClass('selected');
                    }
                });
            });
        }

        private getSizeValue(content: api.content.Content, propertyName: string): number {
            var value = 0,
                metaData = content.getContentData().getProperty('metadata');

            if (metaData && api.data.ValueTypes.DATA.equals(metaData.getType())) {
                value = parseInt(metaData.getPropertySet().getProperty(propertyName).getString());
            }
            else {
                var allExtraData = content.getAllExtraData();
                allExtraData.forEach((extraData: ExtraData) => {
                    if (!value && extraData.getData().getProperty(propertyName)) {
                        value = parseInt(extraData.getData().getProperty(propertyName).getValue().getString());
                    }
                });
            }

            return value;
        }

        setOriginalDimensions(content: api.content.Content) {
            this.originalWidth = this.getSizeValue(content, "imageWidth") || this.initialWidth;
            this.originalHeight = this.getSizeValue(content, "imageHeight");
        }

        private getProportionalHeight(): number {
            if (!this.originalHeight || !this.originalWidth) {
                return 0;
            }
            return Math.round(this.initialWidth * this.originalHeight / this.originalWidth);
        }

        private createImageEditor(imgUrl: string): ImageEditor {

            this.getResultContainer().getEl().setHeightPx(this.getProportionalHeight());
            this.getResultContainer().getEl().addClass("placeholder");

            var imageEditor = new ImageEditor(imgUrl);
            imageEditor.onFocusModeChanged((edit: boolean, position: Point) => {
                imageEditor.removeClass('selected');
                this.notifyFocusEditModeChanged(edit, position);
            });
            imageEditor.onFocusAutoPositionedChanged((auto: boolean) => this.notifyFocusAutoPositionedChanged(auto));
            imageEditor.onCropModeChanged((edit, crop, zoom) => {
                imageEditor.removeClass('selected');
                this.notifyCropEditModeChanged(edit, crop, zoom);
            });
            imageEditor.onCropAutoPositionedChanged((auto: boolean) => this.notifyCropAutoPositionedChanged(auto));

            imageEditor.getImage().onLoaded((event: UIEvent) => {
                this.getResultContainer().getEl().removeClass("placeholder");
            });

            imageEditor.getUploadButton().onClicked(() => {
                wemjq(this.getDropzone().getEl().getHTMLElement()).simulate("click");
            });

            imageEditor.getLastButtonInContainer().onBlur(() => {
                this.toggleSelected(imageEditor);
            });

            return imageEditor;
        }

        createResultItem(value: string): api.dom.DivEl {
            if (!this.initialWidth) {
                this.initialWidth = this.getParentElement().getEl().getWidth();
            }

            var imgUrl = new ContentImageUrlResolver().
                setContentId(new api.content.ContentId(value)).
                setTimestamp(new Date()).
                setSource(true).
                resolve();

            var imageEditor = this.createImageEditor(imgUrl);

            this.imageEditors.push(imageEditor);

            return imageEditor;
        }

        private toggleSelected(imageEditor: ImageEditor) {
            imageEditor.toggleClass('selected');
        }

        setFocalPoint(x: number, y: number) {
            this.imageEditors.forEach((editor: ImageEditor) => {
                editor.setFocusPosition(x, y);
            })
        }

        setCrop(crop: Rect) {
            this.imageEditors.forEach((editor: ImageEditor) => {
                editor.setCropPosition(crop.x, crop.y, crop.x2, crop.y2);
            })
        }

        setZoom(zoom: Rect) {
            this.imageEditors.forEach((editor: ImageEditor) => {
                editor.setZoomPosition(zoom.x, zoom.y, zoom.x2, zoom.y2);
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

        onCropAutoPositionedChanged(listener: (auto: boolean) => void) {
            this.cropAutoPositionedListeners.push(listener);
        }

        unCropAutoPositionedChanged(listener: (auto: boolean) => void) {
            this.cropAutoPositionedListeners = this.cropAutoPositionedListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyCropAutoPositionedChanged(auto: boolean) {
            this.cropAutoPositionedListeners.forEach((listener) => listener(auto));
        }

        onFocusEditModeChanged(listener: (edit: boolean, position: Point) => void) {
            this.focusEditModeListeners.push(listener);
        }

        unFocusEditModeChanged(listener: (edit: boolean, position: Point) => void) {
            this.focusEditModeListeners = this.focusEditModeListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyFocusEditModeChanged(edit: boolean, position: Point) {
            this.focusEditModeListeners.forEach((listener) => {
                listener(edit, position);
            })
        }

        onFocusAutoPositionedChanged(listener: (auto: boolean) => void) {
            this.focusAutoPositionedListeners.push(listener);
        }

        unFocusAutoPositionedChanged(listener: (auto: boolean) => void) {
            this.focusAutoPositionedListeners = this.focusAutoPositionedListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyFocusAutoPositionedChanged(auto: boolean) {
            this.focusAutoPositionedListeners.forEach((listener) => listener(auto));
        }

    }
}