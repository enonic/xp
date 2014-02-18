module LiveEdit.component {
    export class ImagePlaceholder extends ComponentPlaceholder {

        private comboBox:api.content.ContentComboBox;

        constructor() {
            this.setComponentType(new ComponentType(Type.IMAGE));
            super();
            var uploaderConfig = {
                multiSelection: true,
                buttonsVisible: false,
                showImageAfterUpload: false,
                browseEnabled: true
            };

            $(this.getHTMLElement()).on('click', 'input', (e) => {
                $(e.currentTarget).focus();
                e.stopPropagation();
            });

            var imageUploader = new api.ui.ImageUploader("image-selector-upload-dialog", api.util.getRestUri("upload"), uploaderConfig);
            imageUploader.addListener({
                onFileUploaded: (uploadItem:api.ui.UploadItem) => {
                    console.log("file is uploaded", arguments);
                },
                onUploadComplete: () => {
                    console.log("upload complete", arguments);
                }
            });
            this.getEl().setData('live-edit-type', "image");
            this.comboBox = new api.content.ContentComboBoxBuilder().build();
            this.comboBox.hide();
            this.appendChild(this.comboBox);

            this.comboBox.addOptionSelectedListener((item) => {
                var componentPath = this.getComponentPath();
                $liveEdit(window).trigger('imageComponentSetImage.liveEdit', [item.value, componentPath, this]);
            });

            this.getEl().setData('live-edit-type', 'image');
        }

        onSelect() {
            super.onSelect();
            this.comboBox.show();
            this.comboBox.giveFocus();
        }

        onDeselect() {
            super.onDeselect();
            this.comboBox.hide();
        }
    }
}