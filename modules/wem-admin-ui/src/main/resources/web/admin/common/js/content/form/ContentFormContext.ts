module api.content.form {

    export class ContentFormContext extends api.form.FormContext {

        private parentContent: api.content.Content;

        private persistedContent: api.content.Content;

        private attachments: api.content.attachment.Attachments;

        constructor(builder: ContentFormContextBuilder) {
            super(builder);
            this.parentContent = builder.parentContent;
            this.persistedContent = builder.persistedContent;
            this.attachments = builder.attachments;
        }

        getContentId(): api.content.ContentId {
            return this.persistedContent != null ? this.persistedContent.getContentId() : null;
        }

        getContentPath(): api.content.ContentPath {
            return this.persistedContent != null ? this.persistedContent.getPath() : null;
        }

        getParentContentPath(): api.content.ContentPath {

            if (this.parentContent == null) {
                return api.content.ContentPath.ROOT;
            }

            return this.parentContent.getPath();
        }

        getAttachments(): api.content.attachment.Attachments {
            return this.attachments;
        }

        createInputTypeViewContext(inputTypeConfig: any, parentDataPath: api.data.DataPath,
                                  input: api.form.Input): api.form.inputtype.InputTypeViewContext<any> {
            return <api.content.form.inputtype.ContentInputTypeViewContext<any>> {
                input: input,
                inputConfig: inputTypeConfig,
                parentDataPath: parentDataPath,
                contentId: this.getContentId(),
                contentPath: this.getContentPath(),
                parentContentPath: this.getParentContentPath(),
                attachments: this.getAttachments()
            };
        }
    }

    export class ContentFormContextBuilder extends api.form.FormContextBuilder{

        parentContent: api.content.Content;

        persistedContent: api.content.Content;

        attachments: api.content.attachment.Attachments;

        public setParentContent(value: api.content.Content): ContentFormContextBuilder {
            this.parentContent = value;
            return this;
        }

        public setPersistedContent(value: api.content.Content): ContentFormContextBuilder {
            this.persistedContent = value;
            return this;
        }

        public setAttachments(value: api.content.attachment.Attachments): ContentFormContextBuilder {
            this.attachments = value;
            return this;
        }

        public build(): ContentFormContext {
            return new ContentFormContext(this);
        }
    }
}