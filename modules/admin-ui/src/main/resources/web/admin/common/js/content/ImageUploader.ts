module api.content {

    import Button = api.ui.button.Button;
    import CloseButton = api.ui.button.CloseButton;

    export interface ImageUploaderConfig extends MediaUploaderConfig {
        scaleWidth: boolean;
    }

    export class ImageUploader extends MediaUploader {

        private images: api.dom.ImgEl[];
        private focalEditors: api.ui.image.FocalEditor[];
        private focalEditModeListeners: {(edit: boolean, position: {x: number; y: number}): void}[];

        private initialWidth: number;

        private scaleWidth: boolean = false; // parameter states if width of the image must be preferred over its height during resolving

        constructor(config: ImageUploaderConfig) {
            super(config);

            this.images = [];
            this.focalEditors = [];
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

            var focalEditor = new api.ui.image.FocalEditor(imgUrl);
            focalEditor.onEditModeChanged((edit, position) => {
                this.setResetVisible(!edit);
                var shader = api.liveedit.Shader.get();
                if (edit) {
                    shader.shade(focalEditor);
                } else {
                    shader.hide();
                }
                this.notifyFocalPointEditModeChanged(edit, position);
            });
            var image = focalEditor.getImage();

            this.getEl().setMaxWidthPx(image.getEl().getNaturalWidth());

            image.onLoaded(() => {
                this.getEl().setMaxWidthPx(image.getEl().getNaturalWidth());
            });
            image.onRemoved(() => {
                this.getEl().setMaxWidthPx(this.initialWidth);
            });

            focalEditor.onClicked((event: MouseEvent) => {
                focalEditor.toggleClass('selected');

                event.stopPropagation();
                event.preventDefault();
            });

            this.focalEditors.push(focalEditor);

            api.dom.Body.get().onClicked((event: MouseEvent) => {
                this.focalEditors.forEach((editor) => {
                    editor.removeClass('selected');
                });
            });

            return focalEditor;
        }

        setFocalPoint(x: number, y: number) {
            this.focalEditors.forEach((editor: api.ui.image.FocalEditor) => {
                editor.setPosition(x, y);
            })
        }

        isFocalPointEditMode(): boolean {
            return this.focalEditors.some((editor: api.ui.image.FocalEditor) => {
                return editor.isEditMode();
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