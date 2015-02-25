module api.content.form {

    import PropertyPath = api.data.PropertyPath;
    import PropertyArray = api.data.PropertyArray;

    export class ContentFormContext extends api.form.FormContext {

        private site: api.content.site.Site;

        private parentContent: api.content.Content;

        private persistedContent: api.content.Content;

        constructor(builder: ContentFormContextBuilder) {
            super(builder);
            this.site = builder.site;
            this.parentContent = builder.parentContent;
            this.persistedContent = builder.persistedContent;
        }

        getSite(): api.content.site.Site {
            return this.site;
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

        getpersistedContent(): api.content.Content {
            return this.persistedContent;
        }

        createInputTypeViewContext(inputTypeConfig: any, parentPropertyPath: PropertyPath,
                                   input: api.form.Input): api.form.inputtype.InputTypeViewContext<any> {

            return <api.content.form.inputtype.ContentInputTypeViewContext<any>> {
                formContext: this,
                input: input,
                inputConfig: inputTypeConfig,
                parentDataPath: parentPropertyPath,
                site: this.getSite(),
                contentId: this.getContentId(),
                contentPath: this.getContentPath(),
                parentContentPath: this.getParentContentPath()
            };
        }

        static create(): ContentFormContextBuilder {
            return new ContentFormContextBuilder();
        }
    }

    class ContentFormContextBuilder extends api.form.FormContextBuilder {

        site: api.content.site.Site;

        parentContent: api.content.Content;

        persistedContent: api.content.Content;

        attachments: api.content.attachment.Attachments;

        public setSite(value: api.content.site.Site): ContentFormContextBuilder {
            this.site = value;
            return this;
        }

        public setParentContent(value: api.content.Content): ContentFormContextBuilder {
            this.parentContent = value;
            return this;
        }

        public setPersistedContent(value: api.content.Content): ContentFormContextBuilder {
            this.persistedContent = value;
            return this;
        }

        public build(): ContentFormContext {
            return new ContentFormContext(this);
        }
    }
}