module api.liveedit.image {

    import ImageUploadedEvent = api.liveedit.ImageUploadedEvent;
    import ImageOpenUploadDialogEvent = api.liveedit.ImageOpenUploadDialogEvent;
    import PageItemType = api.liveedit.PageItemType;
    import ContentTypeName = api.schema.content.ContentTypeName;
    import ImageComponent = api.content.page.region.ImageComponent;

    export class ImagePlaceholder extends api.liveedit.ComponentPlaceholder {

        private imageComponentView: ImageComponentView;

        private comboBox: api.content.ContentComboBox;

        private uploadButton: api.dom.ButtonEl;

        private comboboxWrapper: api.dom.DivEl;

        constructor(imageView: ImageComponentView) {
            super();
            this.addClass("image-placeholder");
            this.imageComponentView = imageView;

            var imageUploadHandler = (event: ImageUploadedEvent) => {
                if (event.getTargetImagePlaceholder() === this) {
                    var createdImage = event.getUploadedItem();

                    var component: ImageComponent = this.imageComponentView.getComponent();
                    component.setImage(createdImage.getContentId(), createdImage.getDisplayName());
                }
            };
            ImageUploadedEvent.on(imageUploadHandler);
            this.onRemoved((event: api.dom.ElementRemovedEvent) => ImageUploadedEvent.un(imageUploadHandler));

            this.comboboxWrapper = new api.dom.DivEl('rich-combobox-wrapper');
            this.uploadButton = new api.dom.ButtonEl("button upload-button");
            this.uploadButton.addClass("upload-button");
            this.uploadButton.onClicked((event: MouseEvent) => {
                event.preventDefault();
                event.stopPropagation();

                new ImageOpenUploadDialogEvent(this).fire();
            });

            this.comboBox = api.content.ContentComboBox.create().
                setMaximumOccurrences(1).
                setAllowedContentTypes([ContentTypeName.IMAGE.toString()]).
                setLoader(new api.content.ContentSummaryLoader()).
                setMinWidth(270).
                build();

            this.comboboxWrapper.appendChildren(this.comboBox, this.uploadButton);
            this.appendChild(this.comboboxWrapper);

            this.comboBox.onOptionSelected((event: api.ui.selector.OptionSelectedEvent<api.content.ContentSummary>) => {

                var component: ImageComponent = this.imageComponentView.getComponent();
                var imageContent = event.getOption().displayValue;

                component.setImage(imageContent.getContentId(), imageContent.getDisplayName());
                
                this.uploadButton.hide();
                this.imageComponentView.showLoadingSpinner();
            });
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