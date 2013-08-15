module app_new {

    export class NewContentEvent extends api_event.Event {

        private contentType:api_remote_contenttype.ContentType;

        private parentContent:api_remote_content.Content;

        constructor(contentType:api_remote_contenttype.ContentType, parentContent:api_remote_content.Content) {
            super('newContent');
            this.contentType = contentType;
            this.parentContent = parentContent;
        }

        getContentType():api_remote_contenttype.ContentType {
            return this.contentType;
        }

        getParentContent():api_remote_content.Content {
            return this.parentContent;
        }

        static on(handler:(event:NewContentEvent) => void) {
            api_event.onEvent('newContent', handler);
        }
    }

}