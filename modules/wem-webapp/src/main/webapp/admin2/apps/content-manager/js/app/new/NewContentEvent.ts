module app_new {

    export class NewContentEvent extends api_event.Event {

        private contentType:api_remote_contenttype.ContentType;

        constructor(contentType:api_remote_contenttype.ContentType) {
            super('newContent');
            this.contentType = contentType;
        }

        getContentType():api_remote_contenttype.ContentType {
            return this.contentType;
        }

        static on(handler:(event:NewContentEvent) => void) {
            api_event.onEvent('newContent', handler);
        }
    }

}