module app_new {

    export class NewContentEvent extends api_event.Event {

        private contentType:api_remote_contenttype.ContentTypeListNode;

        constructor(contentType:api_remote_contenttype.ContentTypeListNode) {
            super('newContent');
            this.contentType = contentType;
        }

        getContentType():api_remote_contenttype.ContentTypeListNode {
            return this.contentType;
        }

        static on(handler:(event:NewContentEvent) => void) {
            api_event.onEvent('newContent', handler);
        }
    }

}