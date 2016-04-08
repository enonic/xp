module api.content {

    import Button = api.ui.button.Button;
    import CloseButton = api.ui.button.CloseButton;

    import Point = api.ui.image.Point;
    import Rect = api.ui.image.Rect;
    import ImageEditor = api.ui.image.ImageEditor;

    export interface ImageUploaderElConfig extends MediaUploaderElConfig {
        scaleWidth: boolean;
    }

    export class ImageUploaderEl extends MediaUploaderEl {

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

        constructor(config: ImageUploaderElConfig) {
            if (config.allowTypes == undefined) {
                config.allowTypes = [
                    {title: 'Image files', extensions: 'jpg,jpeg,gif,png'}
                ];
            }
            if (config.dropAlwaysAllowed == undefined) {
                config.dropAlwaysAllowed = true;
            }
            if (config.dropzoneAlwaysVisible == undefined) {
                config.dropzoneAlwaysVisible = true;
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

            this.addClass('image-uploader-el');

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
                    if (this.imageEditors.length && !this.imageEditors[0].hasClass(ImageUploaderEl.SELECTED_CLASS)) {
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
                    if (imageEditor.hasClass(ImageUploaderEl.SELECTED_CLASS) && imageEditor.getImage().getHTMLElement() !== event.target) {
                        imageEditor.removeClass(ImageUploaderEl.SELECTED_CLASS);
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

        private createImageEditor(value: string): ImageEditor {

            var contentId = new api.content.ContentId(value),
                imgUrl = this.resolveImageUrl(value);

            this.togglePlaceholder(true);

            var imageEditor = new ImageEditor();
            this.subscribeImageEditorOnEvents(imageEditor, contentId);
            imageEditor.setSrc(imgUrl);

            return imageEditor;
        }

        private resolveImageUrl(value: string): string {
            return new ContentImageUrlResolver().
                setContentId(new api.content.ContentId(value)).
                setTimestamp(new Date()).
                setSource(true).
                resolve();
        }

        private subscribeImageEditorOnEvents(imageEditor: ImageEditor, contentId: api.content.ContentId) {
            var focusAutoPositionedChangedHandler = (auto: boolean) => this.notifyFocusAutoPositionedChanged(auto);
            var cropAutoPositionedChangedHandler = (auto: boolean) => this.notifyCropAutoPositionedChanged(auto);
            var editModeChangedHandler = (edit: boolean, position: Rect, zoom: Rect, focus: Point) => {
                this.notifyEditModeChanged(edit, position, zoom, focus);
                this.togglePlaceholder(edit);

                var index = -1;

                if (edit) {
                    index = imageEditor.getSiblingIndex();
                    api.dom.Body.get().appendChild(imageEditor.addClass(ImageUploaderEl.STANDOUT_CLASS));
                    this.positionImageEditor(imageEditor);
                } else {
                    this.getResultContainer().insertChild(imageEditor.removeClass(ImageUploaderEl.STANDOUT_CLASS), index);
                }
            };
            var uploadButtonClickedHandler = () => {
                wemjq(this.getDropzone().getEl().getHTMLElement()).simulate("click");
            };
            var getLastButtonInContainerBlurHandler = () => {
                this.toggleSelected(imageEditor);
            };
            var shaderVisibilityChangedHandler = (visible: boolean) => {
                new api.app.wizard.MaskContentWizardPanelEvent(contentId, visible).fire();
            };

            var imageErrorHandler = (event: UIEvent) => {
                new api.content.ImageErrorEvent(contentId).fire();
                this.imageEditors = this.imageEditors.filter((curr) => {
                    return curr !== imageEditor;
                })
                api.notify.showError('Failed to upload an image ' + contentId.toString());
            };

            imageEditor.getImage().onLoaded((event: UIEvent) => {
                this.togglePlaceholder(false);
                imageEditor.onShaderVisibilityChanged(shaderVisibilityChangedHandler);
                imageEditor.onEditModeChanged(editModeChangedHandler);
                imageEditor.onFocusAutoPositionedChanged(focusAutoPositionedChangedHandler);
                imageEditor.onCropAutoPositionedChanged(cropAutoPositionedChangedHandler);
                imageEditor.getUploadButton().onClicked(uploadButtonClickedHandler);
                imageEditor.getLastButtonInContainer().onBlur(getLastButtonInContainerBlurHandler);
            });

            imageEditor.onImageError(imageErrorHandler);

            imageEditor.onRemoved(() => {
                imageEditor.unShaderVisibilityChanged(shaderVisibilityChangedHandler);
                imageEditor.unEditModeChanged(editModeChangedHandler);
                imageEditor.unFocusAutoPositionedChanged(focusAutoPositionedChangedHandler);
                imageEditor.unCropAutoPositionedChanged(cropAutoPositionedChangedHandler);
                imageEditor.getUploadButton().unClicked(uploadButtonClickedHandler);
                imageEditor.getLastButtonInContainer().unBlur(getLastButtonInContainerBlurHandler);
                imageEditor.unImageError(imageErrorHandler);
            });
        }

        private positionImageEditor(imageEditor: ImageEditor) {
            var resultOffset = this.getResultContainer().getEl().getOffset();

            imageEditor.getEl().setTopPx(resultOffset.top).
                setLeftPx(resultOffset.left);
        }

        protected getExistingItem(value: string): api.dom.Element {
            return this.imageEditors.filter(elem => {
                return !!elem.getSrc() && elem.getSrc().indexOf(value) > -1;
            })[0];
        }

        protected refreshExistingItem(existingItem: api.dom.Element, value: string) {
            for (var i = 0; i < this.imageEditors.length; i++) {
                var editor = this.imageEditors[i];
                if (existingItem == editor) {
                    editor.setSrc(this.resolveImageUrl(value));
                    break;
                }
            }
        }

        createResultItem(value: string): api.dom.DivEl {

            if (!this.initialWidth) {
                this.initialWidth = this.getParentElement().getEl().getWidth();
            }

            var imageEditor = this.createImageEditor(value);

            this.imageEditors.push(imageEditor);

            return imageEditor;
        }

        private toggleSelected(imageEditor: ImageEditor) {
            imageEditor.toggleClass(ImageUploaderEl.SELECTED_CLASS);
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