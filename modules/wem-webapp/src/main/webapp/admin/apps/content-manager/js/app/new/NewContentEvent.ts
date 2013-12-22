module app_new {

    export class NewContentEvent extends api_event.Event {

        private contentType:api_schema_content.ContentTypeSummary;

        private parentContent:api_content.Content;

        private siteTemplate:api_content_site_template.SiteTemplateSummary;

        constructor(contentType:api_schema_content.ContentTypeSummary, parentContent:api_content.Content, siteTemplate?:api_content_site_template.SiteTemplateSummary) {
            super('newContent');
            this.contentType = contentType;
            this.parentContent = parentContent;
            this.siteTemplate = siteTemplate;
        }

        getContentType():api_schema_content.ContentTypeSummary {
            return this.contentType;
        }

        getParentContent():api_content.Content {
            return this.parentContent;
        }

        getSiteTemplate():api_content_site_template.SiteTemplateSummary {
            return this.siteTemplate;
        }

        static on(handler:(event:NewContentEvent) => void) {
            api_event.onEvent('newContent', handler);
        }
    }

}