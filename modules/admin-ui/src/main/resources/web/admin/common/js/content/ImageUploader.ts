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
        private editModeListeners: {(edit: boolean, crop: Rect, zoom: Rect, focus: Point): void}[];
        private focusAutoPositionedListeners: {(auto: boolean): void}[];
        private cropAutoPositionedListeners: {(auto: boolean): void}[];

        private initialWidth: number;
        private originalHeight: number;
        private originalWidth: number;

        private static SELECTED_CLASS = 'selected';
        private static STANDOUT_CLASS = 'standout';

        private scaleWidth: boolean; // parameter states if width of the image must be preferred over its height during resolving

        constructor(config: ImageUploaderConfig) {
            if (config.allowTypes == undefined) {
                config.allowTypes = [
                    {title: 'Image files', extensions: 'jpg,jpeg,gif,png'}
                ];
            }
            if (config.dropAlwaysAllowed == undefined) {
                config.dropAlwaysAllowed = true;
            }

            super(config);

            this.scaleWidth = false;
            this.imageEditors = [];
            this.editModeListeners = [];
            this.focusAutoPositionedListeners = [];
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
                    if (this.imageEditors.length && !this.imageEditors[0].hasClass(ImageUploader.SELECTED_CLASS)) {
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
                    if (imageEditor.hasClass(ImageUploader.SELECTED_CLASS) && imageEditor.getImage().getHTMLElement() !== event.target) {
                        imageEditor.removeClass(ImageUploader.SELECTED_CLASS);
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

        private togglePlaceholder(flag: boolean) {
            var resultEl = this.getResultContainer().toggleClass('placeholder', flag).getEl();
            if (flag) {
                resultEl.setHeightPx(resultEl.getHeight() || this.getProportionalHeight());
            } else {
                resultEl.setHeight('auto');
            }
        }

        private createImageEditor(imgUrl: string): ImageEditor {

            this.togglePlaceholder(true);

            var imageEditor = new ImageEditor(imgUrl);
            imageEditor.onEditModeChanged((edit: boolean, crop: Rect, zoom: Rect, focus: Point) => {
                this.notifyEditModeChanged(edit, crop, zoom, focus);
            });
            imageEditor.onFocusAutoPositionedChanged((auto: boolean) => this.notifyFocusAutoPositionedChanged(auto));
            imageEditor.onCropAutoPositionedChanged((auto: boolean) => this.notifyCropAutoPositionedChanged(auto));

            imageEditor.getImage().onLoaded((event: UIEvent) => {
                this.togglePlaceholder(false);
            });

            imageEditor.getUploadButton().onClicked(() => {
                wemjq(this.getDropzone().getEl().getHTMLElement()).simulate("click");
            });

            imageEditor.getLastButtonInContainer().onBlur(() => {
                this.toggleSelected(imageEditor);
            });

            var wasSelected;
            imageEditor.getImage().onDragEnter((event) => {
                wasSelected = this.hasClass(ImageUploader.SELECTED_CLASS);
                if (!wasSelected) {
                    imageEditor.addClass(ImageUploader.SELECTED_CLASS);
                }
            });
            imageEditor.getImage().onDragLeave((event) => {
                if (!wasSelected) {
                    imageEditor.removeClass(ImageUploader.SELECTED_CLASS);
                }
            });

            var index = -1;
            imageEditor.onEditModeChanged((edit: boolean, position: Rect, zoom: Rect, focus: Point) => {
                this.togglePlaceholder(edit);

                if (edit) {
                    index = imageEditor.getSiblingIndex();
                    api.dom.Body.get().appendChild(imageEditor.addClass(ImageUploader.STANDOUT_CLASS));
                    this.centerImageEditor(imageEditor);
                } else {
                    this.getResultContainer().insertChild(imageEditor.removeClass(ImageUploader.STANDOUT_CLASS), index);
                }
            });

            api.ui.responsive.ResponsiveManager.onAvailableSizeChanged(api.dom.Body.get(), (item) => {
                if (imageEditor.isEditMode()) {
                    this.centerImageEditor(imageEditor);
                }
            });

            return imageEditor;
        }

        private centerImageEditor(imageEditor: ImageEditor) {
            var imageEl = imageEditor.getEl(),
                win = api.dom.WindowDOM.get();

            imageEditor.getEl().setTopPx((win.getHeight() - imageEl.getHeight()) / 2).
                setLeftPx((win.getWidth() - imageEl.getWidth()) / 2);
        }

        createResultItem(value: string): api.dom.DivEl {
            if (!this.initialWidth) {
                this.initialWidth = this.getParentElement().getEl().getWidth();
            }

            var contentId = new api.content.ContentId(value),
                imgUrl = new ContentImageUrlResolver().
                    setContentId(contentId).
                    setTimestamp(new Date()).
                    setSource(true).
                    resolve();

            var imageEditor = this.createImageEditor(imgUrl);

            imageEditor.onShaderVisibilityChanged((visible: boolean) => {
                new api.app.wizard.MaskContentWizardPanelEvent(contentId, visible).fire();
            });

            this.imageEditors.push(imageEditor);

            return imageEditor;
        }

        private toggleSelected(imageEditor: ImageEditor) {
            imageEditor.toggleClass(ImageUploader.SELECTED_CLASS);
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

        onEditModeChanged(listener: (edit: boolean, crop: Rect, zoom: Rect, focus: Point) => void) {
            this.editModeListeners.push(listener);
        }

        unEditModeChanged(listener: (edit: boolean, crop: Rect, zoom: Rect, focus: Point) => void) {
            this.editModeListeners = this.editModeListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyEditModeChanged(edit: boolean, crop: Rect, zoom: Rect, focus: Point) {
            this.editModeListeners.forEach((listener) => {
                listener(edit, crop, zoom, focus);
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