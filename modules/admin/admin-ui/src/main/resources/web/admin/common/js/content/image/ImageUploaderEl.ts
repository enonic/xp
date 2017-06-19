module api.content.image {

    import Button = api.ui.button.Button;
    import CloseButton = api.ui.button.CloseButton;

    import Point = api.ui.image.Point;
    import Rect = api.ui.image.Rect;
    import ImageEditor = api.ui.image.ImageEditor;
    import i18n = api.util.i18n;

    export class ImageUploaderEl extends api.ui.uploader.MediaUploaderEl {

        private imageEditors: ImageEditor[];
        private editModeListeners: {(edit: boolean, crop: Rect, zoom: Rect, focus: Point): void}[];
        private focusAutoPositionedListeners: {(auto: boolean): void}[];
        private cropAutoPositionedListeners: {(auto: boolean): void}[];

        private initialWidth: number;
        private originalHeight: number;
        private originalWidth: number;

        private static SELECTED_CLASS: string = 'selected';
        private static STANDOUT_CLASS: string = 'standout';

        constructor(config: api.ui.uploader.MediaUploaderElConfig) {
            if (config.allowTypes == null) {
                config.allowTypes = [
                    {title: 'Image files', extensions: 'jpg,jpeg,gif,png,svg'}
                ];
            }
            if (config.selfIsDropzone == null) {
                config.selfIsDropzone = true;
            }

            super(config);

            this.imageEditors = [];
            this.editModeListeners = [];
            this.focusAutoPositionedListeners = [];
            this.cropAutoPositionedListeners = [];

            this.addClass('image-uploader-el');
            this.getEl().setAttribute('data-drop', i18n('drop.image'));
            this.getResultContainer().getEl().setAttribute('data-drop', i18n('drop.file.short'));

            this.initialWidth = 0;
            this.onShown(() => {

                if (this.getEl().getWidth() === 0) {
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
            let value = 0;
            let metaData = content.getContentData().getProperty('metadata');

            if (metaData && api.data.ValueTypes.DATA.equals(metaData.getType())) {
                return parseInt(metaData.getPropertySet().getProperty(propertyName).getString(), 10);
            } else {
                let allExtraData = content.getAllExtraData();
                allExtraData.forEach((extraData: ExtraData) => {
                    if (!value && extraData.getData().getProperty(propertyName)) {
                        value = parseInt(extraData.getData().getProperty(propertyName).getValue().getString(), 10);
                    }
                });
            }

            return value;
        }

        setOriginalDimensions(content: api.content.Content) {
            this.originalWidth = this.getSizeValue(content, 'imageWidth') || this.initialWidth;
            this.originalHeight = this.getSizeValue(content, 'imageHeight');
        }

        private getProportionalHeight(): number {
            if (!this.originalHeight || !this.originalWidth) {
                return 0;
            }
            return Math.round(this.initialWidth * this.originalHeight / this.originalWidth);
        }

        private togglePlaceholder(flag: boolean) {
            let resultEl = this.getResultContainer().toggleClass('placeholder', flag).getEl();
            if (flag) {
                resultEl.setHeightPx(resultEl.getHeight() || this.getProportionalHeight());
            } else {
                resultEl.setHeight('auto');
            }
        }

        private createImageEditor(value: string): ImageEditor {

            let contentId = new api.content.ContentId(value);
            let imgUrl = this.resolveImageUrl(value);

            this.togglePlaceholder(true);

            let imageEditor = new ImageEditor();
            this.subscribeImageEditorOnEvents(imageEditor, contentId);
            imageEditor.setSrc(imgUrl);

            return imageEditor;
        }

        private resolveImageUrl(value: string): string {
            return new api.content.util.ContentImageUrlResolver()
                .setContentId(new api.content.ContentId(value))
                .setTimestamp(new Date())
                .setSource(true)
                .resolve();
        }

        private subscribeImageEditorOnEvents(imageEditor: ImageEditor, contentId: api.content.ContentId) {
            let focusAutoPositionedChangedHandler = (auto: boolean) => this.notifyFocusAutoPositionedChanged(auto);
            let cropAutoPositionedChangedHandler = (auto: boolean) => this.notifyCropAutoPositionedChanged(auto);
            let editModeChangedHandler = (edit: boolean, position: Rect, zoom: Rect, focus: Point) => {
                this.notifyEditModeChanged(edit, position, zoom, focus);
                this.togglePlaceholder(edit);

                let index = -1;

                if (edit) {
                    index = imageEditor.getSiblingIndex();
                    api.dom.Body.get().appendChild(imageEditor.addClass(ImageUploaderEl.STANDOUT_CLASS));
                    this.positionImageEditor(imageEditor);
                } else {
                    this.getResultContainer().insertChild(imageEditor.removeClass(ImageUploaderEl.STANDOUT_CLASS), index);
                }
            };
            let uploadButtonClickedHandler = () => {
                this.showFileSelectionDialog();
            };
            let getLastButtonInContainerBlurHandler = () => {
                this.toggleSelected(imageEditor);
            };
            let shaderVisibilityChangedHandler = (visible: boolean) => {
                new api.app.wizard.MaskContentWizardPanelEvent(contentId, visible).fire();
            };

            let imageErrorHandler = (event: UIEvent) => {
                new ImageErrorEvent(contentId).fire();
                this.imageEditors = this.imageEditors.filter((curr) => {
                    return curr !== imageEditor;
                });
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
            let resultOffset = this.getResultContainer().getEl().getOffset();

            imageEditor.getEl().setTopPx(resultOffset.top).setLeftPx(resultOffset.left);
        }

        protected getExistingItem(value: string): api.dom.Element {
            return this.imageEditors.filter(elem => {
                return !!elem.getSrc() && elem.getSrc().indexOf(value) > -1;
            })[0];
        }

        protected refreshExistingItem(existingItem: api.dom.Element, value: string) {
            for (let i = 0; i < this.imageEditors.length; i++) {
                let editor = this.imageEditors[i];
                if (existingItem === editor) {
                    editor.setSrc(this.resolveImageUrl(value));
                    break;
                }
            }
        }

        createResultItem(value: string): api.dom.DivEl {

            if (!this.initialWidth) {
                this.initialWidth = this.getParentElement().getEl().getWidth();
            }

            let imageEditor = this.createImageEditor(value);

            this.imageEditors.push(imageEditor);

            return imageEditor;
        }

        private toggleSelected(imageEditor: ImageEditor) {
            imageEditor.toggleClass(ImageUploaderEl.SELECTED_CLASS);
        }

        setFocalPoint(focal: Point) {
            this.imageEditors.forEach((editor: ImageEditor) => {
                if (!!focal) {
                    editor.setFocusPosition(focal.x, focal.y);
                } else {
                    editor.resetFocusPosition();
                }
            });
        }

        setCrop(crop: Rect) {
            this.imageEditors.forEach((editor: ImageEditor) => {
                if (!!crop) {
                    editor.setCropPosition(crop.x, crop.y, crop.x2, crop.y2);
                } else {
                    editor.resetCropPosition();
                }
            });
        }

        setZoom(zoom: Rect) {
            this.imageEditors.forEach((editor: ImageEditor) => {
                if (!!zoom) {
                    editor.setZoomPosition(zoom.x, zoom.y, zoom.x2, zoom.y2);
                } else {
                    editor.resetZoomPosition();
                }
            });
        }

        isFocalPointEditMode(): boolean {
            return this.imageEditors.some((editor: ImageEditor) => {
                return editor.isFocusEditMode();
            });
        }

        isCropEditMode(): boolean {
            return this.imageEditors.some((editor: ImageEditor) => {
                return editor.isCropEditMode();
            });
        }

        protected isSameValueUpdateAllowed(): boolean {
            return true;
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
            });
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
