module app.create {

    import Content = api.content.Content;
    import Attachment = api.content.attachment.Attachment;

    export class NewMediaEvent extends api.event.Event {

        private content: Content;

        private parentContent: Content;

        constructor(content: Content, parentContent: Content) {
            super();
            this.content = content;
            this.parentContent = parentContent;
        }

        getContent(): Content {
            return this.content;
        }

        getParentContent(): Content {
            return this.parentContent;
        }

        static on(handler: (event: NewMediaEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: NewMediaEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }

}