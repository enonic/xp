module app.create {

    import ContentTypeSummary = api.schema.content.ContentTypeSummary;
    import Content = api.content.Content;
    import Attachment = api.content.attachment.Attachment;

    export class NewContentEvent extends api.event.Event {

        private contentType: ContentTypeSummary;

        private parentContent: Content;

        constructor(contentType: ContentTypeSummary, parentContent: Content) {
            super();
            this.contentType = contentType;
            this.parentContent = parentContent;
        }

        getContentType(): ContentTypeSummary {
            return this.contentType;
        }

        getParentContent(): Content {
            return this.parentContent;
        }

        static on(handler: (event: NewContentEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: NewContentEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }

}