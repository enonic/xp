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


            onImageUploaded((event: api.ui.ImageUploadedEvent) => {
                this.createEmbeddedImageContent(event.getUploadedItem());
            });

            var comboUploadButtonDiv = new api.dom.DivEl('image-placeholder-selector');
            this.uploadButton = new api.ui.Button("");
            this.uploadButton.addClass("upload-button");
            this.uploadButton.onClicked(() => {
                openImageUploadDialogRequestListeners.forEach((listener: {():void}) => {
                    listener.call(this);
                });
            });
            this.uploadButton.hide();

            this.getEl().setData('live-edit-type', "image");

            var imageLoader = new api.content.ContentSummaryLoader();
            var allowedContentTypes = ["image"];
            imageLoader.setAllowedContentTypes(allowedContentTypes);
            this.comboBox = new api.content.ContentComboBoxBuilder().
                setMaximumOccurrences(1).
                setAllowedContentTypes(allowedContentTypes).
                setLoader(imageLoader).
                build();
            this.comboBox.addClass('image-placeholder');
            this.comboBox.hide();

            comboUploadButtonDiv.appendChild(this.comboBox);
            comboUploadButtonDiv.appendChild(this.uploadButton);
            this.appendChild(comboUploadButtonDiv);

            this.comboBox.onOptionSelected((event: api.ui.selector.OptionSelectedEvent<api.content.ContentSummary>) => {
                var componentPath = this.getComponentPath();
                $liveEdit(window).trigger('imageComponentSetImage.liveEdit',
                    [event.getOption().value, componentPath, this, event.getOption().displayValue.getDisplayName()]);
            });
        }


        private createEmbeddedImageContent(uploadItem: api.ui.UploadItem) {

            var attachmentName = new api.content.attachment.AttachmentName(uploadItem.getName());
            var attachment = new api.content.attachment.AttachmentBuilder().
                setBlobKey(uploadItem.getBlobKey()).
                setAttachmentName(attachmentName).
                setMimeType(uploadItem.getMimeType()).
                setSize(uploadItem.getSize()).
                build();
            var mimeType = uploadItem.getMimeType();
            new api.schema.content.GetContentTypeByNameRequest(new api.schema.content.ContentTypeName("image")).
                sendAndParse().
                done((contentType: api.schema.content.ContentType) => {

                    var contentData = new api.content.image.ImageContentDataFactory().
                        setImage(attachmentName).
                        setMimeType(mimeType).create();

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
                    createContentRequest.
                        sendAndParse().
                        done((createdContent: api.content.Content) => {
                            var componentPath = this.getComponentPath();
                            $liveEdit(window).trigger('imageComponentSetImage.liveEdit',
                                [createdContent.getId(), componentPath, this, uploadItem.getName()]);

                        });
                });
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