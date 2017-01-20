module api.liveedit.image {

    import PageItemType = api.liveedit.PageItemType;
    import ContentTypeName = api.schema.content.ContentTypeName;
    import ImageComponent = api.content.page.region.ImageComponent;
    import SelectedOptionEvent = api.ui.selector.combobox.SelectedOptionEvent;

    export class ImagePlaceholder extends api.liveedit.ItemViewPlaceholder {

        private imageComponentView: ImageComponentView;

        private comboBox: api.content.ContentComboBox;

        private comboboxWrapper: api.dom.DivEl;

        private imageUploader: api.content.image.ImageUploaderEl;

        constructor(imageView: ImageComponentView) {
            super();
            this.addClassEx('image-placeholder').addClass(api.StyleHelper.getCommonIconCls('image'));
            this.imageComponentView = imageView;

            this.initImageCombobox(imageView);
            this.initImageUploader(imageView);
            this.initImageComboboxWrapper();
        }

        private initImageCombobox(imageView: ImageComponentView) {
            let loader = new api.content.resource.ContentSummaryLoader();
            loader.setContentPath(imageView.getLiveEditModel().getContent().getPath());
            loader.setAllowedContentTypeNames([ContentTypeName.IMAGE, ContentTypeName.MEDIA_VECTOR]);

            this.comboBox = api.content.ContentComboBox.create().
                setMaximumOccurrences(1).
                setLoader(loader).
                setMinWidth(270).
                build();

            this.comboBox.getComboBox().getInput().setPlaceholder('Type to search or drop image here...');
            this.comboBox.onOptionSelected((event: SelectedOptionEvent<api.content.ContentSummary>) => {

                let component: ImageComponent = this.imageComponentView.getComponent();
                let imageContent = event.getSelectedOption().getOption().displayValue;

                component.setImage(imageContent.getContentId(), imageContent.getDisplayName());

                this.imageComponentView.showLoadingSpinner();
            });
        }

        private initImageUploader(imageView: ImageComponentView) {
            this.imageUploader = new api.content.image.ImageUploaderEl({
                params: {
                    parent: imageView.getLiveEditModel().getContent().getContentId().toString()
                },
                operation: api.ui.uploader.MediaUploaderElOperation.create,
                name: 'image-selector-placeholder-upload',
                showCancel: false,
                showResult: false,
                allowMultiSelection: false,
                hideDefaultDropZone: true,
                deferred: true
            });

            this.imageUploader.getUploadButton().onClicked(() => this.comboboxWrapper.show());

            this.imageUploader.onFileUploaded((event: api.ui.uploader.FileUploadedEvent<api.content.Content>) => {
                let createdImage = event.getUploadItem().getModel();

                let component: ImageComponent = this.imageComponentView.getComponent();
                component.setImage(createdImage.getContentId(), createdImage.getDisplayName());
            });

            this.imageUploader.addDropzone(this.comboBox.getId());
        }

        private initImageComboboxWrapper() {
            this.comboboxWrapper = new api.dom.DivEl('rich-combobox-wrapper');
            this.comboboxWrapper.appendChild(this.comboBox);
            this.comboboxWrapper.appendChild(<any>this.imageUploader);
            this.appendChild(this.comboboxWrapper);
        }

        select() {
            this.comboboxWrapper.show();
            this.comboBox.giveFocus();
        }

        deselect() {
            this.comboboxWrapper.hide();
        }
    }
}
