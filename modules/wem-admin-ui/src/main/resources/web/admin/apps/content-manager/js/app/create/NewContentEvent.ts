module app.create {

    export class NewContentEvent extends api.event.Event {

        private contentType: api.schema.content.ContentTypeSummary;

        private parentContent: api.content.Content;

        constructor(contentType: api.schema.content.ContentTypeSummary, parentContent: api.content.Content) {
            super();
            this.contentType = contentType;
            this.parentContent = parentContent;
        }

        getContentType(): api.schema.content.ContentTypeSummary {
            return this.contentType;
        }

        getParentContent(): api.content.Content {
            return this.parentContent;
        }

        isCreateSite(): boolean {
            return this.contentType.isSite();
        }

        static on(handler: (event: NewContentEvent) => void) {
            api.event.Event.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: NewContentEvent) => void) {
            api.event.Event.unbind(api.util.getFullName(this), handler);
        }
    }

}