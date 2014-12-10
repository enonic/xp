module app.create {

    import ContentTypeSummary = api.schema.content.ContentTypeSummary;
    import Content = api.content.Content;
    import Attachment = api.content.attachment.Attachment;

    export class NewContentEvent extends api.event.Event {

        private contentType: ContentTypeSummary;

        private mediaAttachment: Attachment;

        private parentContent: Content;

        constructor(contentType: ContentTypeSummary, parentContent: Content, mediaAttachment?: Attachment) {
            super();
            this.contentType = contentType;
            this.parentContent = parentContent;
            this.mediaAttachment = mediaAttachment;
        }

        getContentType(): ContentTypeSummary {
            return this.contentType;
        }

        getMediaAttachment(): Attachment {
            return this.mediaAttachment;
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