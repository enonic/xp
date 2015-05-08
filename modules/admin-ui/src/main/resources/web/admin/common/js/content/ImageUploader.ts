module api.content {

    import Button = api.ui.button.Button;
    import CloseButton = api.ui.button.CloseButton;

    export interface ImageUploaderConfig extends MediaUploaderConfig {
    }

    export class ImageUploader extends MediaUploader {

        private focalEditors: api.ui.image.FocalEditor[];
        private focalEditModeListeners: {(edit: boolean, position: {x: number; y: number}): void}[];

        private initialWidth: number;

        constructor(config: ImageUploaderConfig) {
            this.focalEditors = [];
            this.focalEditModeListeners = [];

            if (config.allowTypes == undefined) {
                config.allowTypes = [
                    {title: 'Image files', extensions: 'jpg,gif,png'}
                ];
            }

            super(config);
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
                resolve();

            var focalEditor = new api.ui.image.FocalEditor(imgUrl);
            focalEditor.onEditModeChanged((edit, position) => this.notifyFocalEditModeChanged(edit, position));
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

        isFocalEditMode(): boolean {
            return this.focalEditors.some((editor) => {
                return editor.isEditMode();
            });
        }

        onFocalEditModeChanged(listener: (edit: boolean, position: {x: number; y: number}) => void) {
            this.focalEditModeListeners.push(listener);
        }

        unFocalEditModeChanged(listener: (edit: boolean, position: {x: number; y: number}) => void) {
            this.focalEditModeListeners = this.focalEditModeListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyFocalEditModeChanged(edit: boolean, position: {x: number; y: number}) {
            this.focalEditModeListeners.forEach((listener) => {
                listener(edit, position);
            })
        }

    }
}