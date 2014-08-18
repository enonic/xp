module api.liveedit.image {

    import ImageUploadedEvent = api.liveedit.ImageUploadedEvent;
    import ImageOpenUploadDialogEvent = api.liveedit.ImageOpenUploadDialogEvent;
    import PageItemType = api.liveedit.PageItemType;

    export class ImagePlaceholder extends api.dom.DivEl {

        private imageComponentView: ImageComponentView;

        private comboBox: api.content.ContentComboBox;

        private uploadButton: api.dom.DivEl;

        constructor(imageComponentView: ImageComponentView) {
            super();
            this.imageComponentView = imageComponentView;
            this.onClicked((event: MouseEvent) => {
                event.stopPropagation();
            });

            var imageUploadHandler = (event: ImageUploadedEvent) => {
                if (event.getTargetImagePlaceholder() === this) {
                    this.createImageContent(event.getUploadedItem());
                }
            };
            ImageUploadedEvent.on(imageUploadHandler);
            this.onRemoved((event: api.dom.ElementRemovedEvent) => ImageUploadedEvent.un(imageUploadHandler));

            var comboUploadButtonDiv = new api.dom.DivEl('image-placeholder-selector');
            this.uploadButton = new api.dom.DivEl("button upload-button");
            this.uploadButton.addClass("upload-button");
            this.uploadButton.onClicked((event: MouseEvent) => {
                event.preventDefault();
                event.stopPropagation();

                new ImageOpenUploadDialogEvent(this).fire();
            });
            this.uploadButton.hide();

            this.comboBox = new api.content.ContentComboBoxBuilder().
                setMaximumOccurrences(1).
                setAllowedContentTypes(["image"]).
                setLoader(new api.content.ContentSummaryLoader()).
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
                    setImageComponentView(imageComponentView).
                    setName(event.getOption().displayValue.getDisplayName()).
                    fire();

            });
        }


        private createImageContent(uploadItem: api.ui.uploader.UploadItem) {

            this.imageComponentView.showLoadingSpinner();

            new api.schema.content.GetContentTypeByNameRequest(new api.schema.content.ContentTypeName("image")).
                sendAndParse().
                then((contentType: api.schema.content.ContentType) => {

                    var attachmentName = new api.content.attachment.AttachmentName(uploadItem.getName());

                    var attachment = new api.content.attachment.AttachmentBuilder().
                        setBlobKey(uploadItem.getBlobKey()).
                        setAttachmentName(attachmentName).
                        setMimeType(uploadItem.getMimeType()).
                        setSize(uploadItem.getSize()).
                        build();

                    var contentData = new api.content.image.ImageContentDataFactory().
                        setImage(attachmentName).
                        setMimeType(uploadItem.getMimeType()).
                        create();

                    var createContentRequest = new api.content.CreateContentRequest().
                        setDraft(false).
                        setParent(PageItemType.get().getContent().getPath()).
                        setName(api.content.ContentName.fromString(api.content.ContentName.ensureValidContentName(attachmentName.toString()))).
                        setContentType(contentType.getContentTypeName()).
                        setDisplayName(attachmentName.toString()).
                        setForm(contentType.getForm()).
                        setContentData(contentData).
                        addAttachment(attachment);

                    return createContentRequest.sendAndParse();

                }).then((createdContent: api.content.Content) => {

                    new ImageComponentSetImageEvent().
                        setImageId(createdContent.getContentId()).
                        setImageComponentView(this.imageComponentView).
                        setName(uploadItem.getName()).
                        fire();

                }).catch((reason) => {

                    new ImageComponentSetImageEvent().
                        setErrorMessage(reason.message).
                        fire();

                    this.imageComponentView.hideLoadingSpinner();

                }).done();
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