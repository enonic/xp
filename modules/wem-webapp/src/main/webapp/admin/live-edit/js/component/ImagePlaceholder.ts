module LiveEdit.component {
    export class ImagePlaceholder extends ComponentPlaceholder {

        private comboBox: api.content.ContentComboBox;
        private uploadButton: api.ui.Button;

        constructor() {
            this.setComponentType(new ComponentType(Type.IMAGE));
            super();

            $(this.getHTMLElement()).on('click', 'input', (e) => {
                $(e.currentTarget).focus();
                e.stopPropagation();
            });

            var imageUploadHandler = (event: api.ui.ImageUploadedEvent) => this.createEmbeddedImageContent(event.getUploadedItem());
            onImageUploaded(imageUploadHandler);
            this.onRemoved((event: api.dom.ElementRemovedEvent) => unImageUploaded(imageUploadHandler));

            var comboUploadButtonDiv = new api.dom.DivEl('image-placeholder-selector');
            this.uploadButton = new api.ui.Button();
            this.uploadButton.addClass("upload-button");
            this.uploadButton.onClicked(() => notifyOpenImageUploadDialogListeners());
            this.uploadButton.hide();

            this.getEl().setData('live-edit-type', "image");

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
                this.showLoadingSpinner();

                $liveEdit(window).trigger('imageComponentSetImage.liveEdit', [{
                    imageId: event.getOption().value,
                    componentPathAsString: this.getComponentPath(),
                    componentPlaceholder: this,
                    imageName: event.getOption().displayValue.getDisplayName()
                }]);

            });
        }


        private createEmbeddedImageContent(uploadItem: api.ui.UploadItem) {

            this.showLoadingSpinner();

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
                        setParent(content.getPath()).
                        setEmbed(true).
                        setName(api.content.ContentName.fromString(api.content.ContentName.ensureValidContentName(attachmentName.toString()))).
                        setContentType(contentType.getContentTypeName()).
                        setDisplayName(attachmentName.toString()).
                        setForm(contentType.getForm()).
                        setContentData(contentData).
                        addAttachment(attachment);

                    return createContentRequest.sendAndParse();

                }).then((createdContent: api.content.Content) => {

                    $liveEdit(window).trigger('imageComponentSetImage.liveEdit', [{
                        imageId: createdContent.getId(),
                        componentPathAsString: this.getComponentPath(),
                        componentPlaceholder: this,
                        imageName: uploadItem.getName()
                    }]);

                }).catch((reason) => {

                    $liveEdit(window).trigger('imageComponentSetImage.liveEdit', [{
                        errorMessage: reason.message
                    }]);

                    this.hideLoadingSpinner();

                }).done();
        }

        onSelect() {
            super.onSelect();
            this.comboBox.show();
            this.uploadButton.show();
            this.comboBox.giveFocus();
        }

        onDeselect() {
            super.onDeselect();
            this.uploadButton.hide();
            this.comboBox.hide();
        }
    }
}