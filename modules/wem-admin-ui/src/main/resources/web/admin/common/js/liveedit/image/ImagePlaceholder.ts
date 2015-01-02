module api.liveedit.image {

    import ImageUploadedEvent = api.liveedit.ImageUploadedEvent;
    import ImageOpenUploadDialogEvent = api.liveedit.ImageOpenUploadDialogEvent;
    import PageItemType = api.liveedit.PageItemType;
    import ContentTypeName = api.schema.content.ContentTypeName;

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
                    var createdContent = event.getUploadedItem();

                    new api.content.ContentCreatedEvent(createdContent.getContentId()).fire();

                    new ImageComponentSetImageEvent().
                        setImageId(createdContent.getContentId()).
                        setImageComponentView(this.imageComponentView).
                        setName(createdContent.getName().toString()).
                        fire();
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

                this.uploadButton.hide();
                this.imageComponentView.showLoadingSpinner();

                new ImageComponentSetImageEvent().
                    setImageId(event.getOption().displayValue.getContentId()).
                    setImageComponentView(imageView).
                    setName(event.getOption().displayValue.getDisplayName()).
                    fire();

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