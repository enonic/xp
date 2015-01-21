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

        constructor(imageView: ImageComponentView) {
            super();
            this.imageComponentView = imageView;
            this.onClicked((event: MouseEvent) => {
                event.stopPropagation();
            });

            var imageUploadHandler = (event: ImageUploadedEvent) => {
                if (event.getTargetImagePlaceholder() === this) {
                    var createdImage = event.getUploadedItem();

                    new api.content.ContentCreatedEvent(createdImage.getContentId()).fire();

                    var component: ImageComponent = this.imageComponentView.getComponent();
                    component.setImage(createdImage.getContentId(), createdImage.getDisplayName());
                }
            };
            ImageUploadedEvent.on(imageUploadHandler);
            this.onRemoved((event: api.dom.ElementRemovedEvent) => ImageUploadedEvent.un(imageUploadHandler));

            var comboUploadButtonDiv = new api.dom.DivEl('image-placeholder-selector');
            this.uploadButton = new api.dom.ButtonEl("button upload-button");
            this.uploadButton.addClass("upload-button");
            this.uploadButton.onClicked((event: MouseEvent) => {
                event.preventDefault();
                event.stopPropagation();

                new ImageOpenUploadDialogEvent(this).fire();
            });
            this.uploadButton.hide();

            this.comboBox = new api.content.ContentComboBoxBuilder().
                setMaximumOccurrences(1).
                setAllowedContentTypes([ContentTypeName.IMAGE.toString()]).
                setLoader(new api.content.ContentSummaryLoader()).
                setMinWidth(270).
                build();
            this.comboBox.addClass('image-placeholder');
            this.comboBox.hide();

            comboUploadButtonDiv.appendChild(this.comboBox);
            comboUploadButtonDiv.appendChild(this.uploadButton);
            this.appendChild(comboUploadButtonDiv);

            this.comboBox.onOptionSelected((event: api.ui.selector.OptionSelectedEvent<api.content.ContentSummary>) => {

                var component: ImageComponent = this.imageComponentView.getComponent();
                var imageContent = event.getOption().displayValue;

                component.setImage(imageContent.getContentId(), imageContent.getDisplayName());
                
                this.uploadButton.hide();
                this.imageComponentView.showLoadingSpinner();
            });
        }

        select() {
            this.comboBox.show();
            this.uploadButton.show();
            this.comboBox.giveFocus();
        }

        deselect() {
            this.uploadButton.hide();
            this.comboBox.hide();
        }
    }
}