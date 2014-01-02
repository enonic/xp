module app.create {

    export class NewContentEvent extends api.event.Event {

        private contentType:api.schema.content.ContentTypeSummary;

        private parentContent:api.content.Content;

        private siteTemplate:api.content.site.template.SiteTemplateSummary;

        constructor(contentType:api.schema.content.ContentTypeSummary, parentContent:api.content.Content, siteTemplate?:api.content.site.template.SiteTemplateSummary) {
            super('newContent');
            this.contentType = contentType;
            this.parentContent = parentContent;
            this.siteTemplate = siteTemplate;
        }

        getContentType():api.schema.content.ContentTypeSummary {
            return this.contentType;
        }

        getParentContent():api.content.Content {
            return this.parentContent;
        }

        getSiteTemplate():api.content.site.template.SiteTemplateSummary {
            return this.siteTemplate;
        }

        static on(handler:(event:NewContentEvent) => void) {
            api.event.onEvent('newContent', handler);
        }
    }

}