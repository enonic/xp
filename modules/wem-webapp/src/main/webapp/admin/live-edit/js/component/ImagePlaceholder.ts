module LiveEdit.component {
    export class ImagePlaceholder extends ComponentPlaceholder {

        private comboBox:api.content.ContentComboBox;
        private uploadLink: api.dom.AEl;
        private uploadDialog: api.form.inputtype.content.image.UploadDialog;

        constructor() {
            this.setComponentType(new ComponentType(Type.IMAGE));
            super();

            $(this.getHTMLElement()).on('click', 'input', (e) => {
                $(e.currentTarget).focus();
                e.stopPropagation();
            });


            this.uploadDialog = new api.form.inputtype.content.image.UploadDialog();
            this.uploadDialog.onImageUploaded((event: api.ui.ImageUploadedEvent) => {
                this.createEmbeddedImageContent(event.getUploadedItem());
            });

            var divEl = new api.dom.DivEl();
            divEl.addClass("upload-new-image-button");
            this.uploadLink = new api.dom.AEl();
            this.uploadLink.setText('Upload new image');
            this.uploadLink.setUrl('javascript:void');
            this.uploadLink.onClicked(() => {
                this.uploadDialog.open();
            });
            this.uploadLink.hide();
            divEl.appendChild(this.uploadLink)
            this.appendChild(divEl);

            this.getEl().setData('live-edit-type', "image");
            this.comboBox = new api.content.ContentComboBoxBuilder().
                setMaximumOccurrences(1).
                setAllowedContentTypes(["image"]).
                build();
            this.comboBox.hide();
            this.appendChild(this.comboBox);


            this.comboBox.addOptionSelectedListener((item) => {
                var componentPath = this.getComponentPath();
                console.log(item.value, componentPath);
                $liveEdit(window).trigger('imageComponentSetImage.liveEdit', [item.value, componentPath, this]);
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
                            $liveEdit(window).trigger('imageComponentSetImage.liveEdit', [createdContent.getId(), componentPath, this]);

                        });
                });
        }

        onSelect() {
            super.onSelect();
            this.comboBox.show();
            this.uploadLink.show();
            this.comboBox.giveFocus();
        }

        onDeselect() {
            super.onDeselect();
            this.comboBox.hide();
            this.uploadLink.hide();
        }
    }
}