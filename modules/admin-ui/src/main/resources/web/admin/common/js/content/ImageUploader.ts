module api.content {

    import Button = api.ui.button.Button;
    import CloseButton = api.ui.button.CloseButton;

    export interface ImageUploaderConfig extends MediaUploaderConfig {
    }

    export class ImageUploader extends MediaUploader {

        private images: api.dom.ImgEl[];

        private focalPointButton: api.ui.button.Button;
        private setFocusButton: api.ui.button.Button;
        private cancelButton: api.ui.button.Button;
        private editMode: boolean;
        private initialWidth: number;

        private editModeListeners: {(edit: boolean): void}[];

        constructor(config: ImageUploaderConfig) {
            this.images = [];
            this.editMode = false;
            this.editModeListeners = [];

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

            this.onFileUploaded((event: api.ui.uploader.FileUploadedEvent<api.content.Content>) => {
                new api.app.wizard.ContentWizardImageUploadedEvent(event.getUploadItem().getModel(), this).fire();
            });
        }

        createResultItem(value: string): api.dom.DivEl {
            this.initialWidth = this.getParentElement().getEl().getWidth();
            var container = new api.dom.DivEl();

            var imgUrl = new ContentImageUrlResolver().
                setContentId(new api.content.ContentId(value)).
                setSize(this.initialWidth).
                setTimestamp(new Date()).
                resolve();

            var image = new api.dom.ImgEl(imgUrl);
            this.getEl().setMaxWidthPx(image.getEl().getNaturalWidth());

            image.onLoaded(() => {
                this.getEl().setMaxWidthPx(image.getEl().getNaturalWidth());
            });
            image.onRemoved(() => {
                this.getEl().setMaxWidthPx(this.initialWidth);
            });

            image.onClicked((event: MouseEvent) => {
                image.toggleClass('selected');

                event.stopPropagation();
                event.preventDefault();
            });

            container.appendChild(image);
            this.images.push(image);

            container.appendChild(this.createFocalPointToolbar());

            api.dom.Body.get().onClicked((event: MouseEvent) => {
                this.images.forEach((img) => {
                    img.removeClass('selected');
                });
            });

            return container;
        }

        private createFocalPointToolbar(): api.dom.DivEl {
            var toolbar = new api.dom.DivEl('focal-point-toolbar');

            this.focalPointButton = new api.ui.button.Button();
            this.focalPointButton.addClass('no-bg icon-center-focus-strong');
            this.focalPointButton.onClicked((event: MouseEvent) => {
                this.setEditMode(true);
            });
            toolbar.appendChild(this.focalPointButton);

            this.setFocusButton = new api.ui.button.Button('Set Focus');
            this.setFocusButton.addClass('blue');
            this.setFocusButton.setVisible(false);
            this.setFocusButton.onClicked((event: MouseEvent) => {
                this.setEditMode(false);
            });
            this.cancelButton = new api.ui.button.Button('Cancel');
            this.cancelButton.setVisible(false);
            this.cancelButton.onClicked((event: MouseEvent) => {
                this.setEditMode(false);
            });

            var pullRight = new api.dom.DivEl('pull-right');
            pullRight.appendChild(this.setFocusButton);
            pullRight.appendChild(this.cancelButton);
            toolbar.appendChild(pullRight);

            return toolbar;
        }

        private setEditMode(edit: boolean) {
            this.focalPointButton.setVisible(!edit);
            this.setFocusButton.setVisible(edit);
            this.cancelButton.setVisible(edit);

            this.notifyEditModeChanged(edit);
        }

        isEditMode(): boolean {
            return this.editMode;
        }

        onEditModeChanged(listener: (edit: boolean) => void) {
            this.editModeListeners.push(listener);
        }

        unEditModeChanged(listener: (edit: boolean) => void) {
            this.editModeListeners = this.editModeListeners.filter((curr) => {
                return curr !== listener;
            })
        }

        private notifyEditModeChanged(edit: boolean) {
            this.editMode = edit;
            this.editModeListeners.forEach((listener) => {
                listener(edit);
            })
        }

    }
}