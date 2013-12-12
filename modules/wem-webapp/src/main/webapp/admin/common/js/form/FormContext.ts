module api_form {

    export class FormContext {

        private parentContent: api_content.Content;

        private persistedContent: api_content.Content;

        constructor(builder: FormContextBuilder) {
            this.parentContent = builder.parentContent;
            this.persistedContent = builder.persistedContent;
        }

        getContentId(): api_content.ContentId {
            return this.persistedContent.getContentId();
        }

        getContentPath(): api_content.ContentPath {
            return this.persistedContent.getPath();
        }

        getParentContentPath(): api_content.ContentPath {

            if (this.parentContent == null) {
                return api_content.ContentPath.ROOT;
            }

            return this.parentContent.getPath();
        }
    }

    export class FormContextBuilder {

        parentContent: api_content.Content;

        persistedContent: api_content.Content;

        public setParentContent(value: api_content.Content): FormContextBuilder {
            this.parentContent = value;
            return this;
        }

        public setPersistedContent(value: api_content.Content): FormContextBuilder {
            this.persistedContent = value;
            return this;
        }

        public build(): FormContext {
            return new FormContext(this);
        }
    }
}