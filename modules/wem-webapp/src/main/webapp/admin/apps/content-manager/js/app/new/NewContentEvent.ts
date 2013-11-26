module app_new {

    export class NewContentEvent extends api_event.Event {

        private contentType:api_schema_content.ContentTypeSummary;

        private parentContent:api_content.Content;

        private siteRoot:boolean;

        constructor(contentType:api_schema_content.ContentTypeSummary, parentContent:api_content.Content, siteRoot?:boolean) {
            super('newContent');
            this.contentType = contentType;
            this.parentContent = parentContent;
            this.siteRoot = siteRoot || false;
        }

        getContentType():api_schema_content.ContentTypeSummary {
            return this.contentType;
        }

        getParentContent():api_content.Content {
            return this.parentContent;
        }

        isSiteRoot():boolean {
            return this.siteRoot;
        }

        static on(handler:(event:NewContentEvent) => void) {
            api_event.onEvent('newContent', handler);
        }
    }

}