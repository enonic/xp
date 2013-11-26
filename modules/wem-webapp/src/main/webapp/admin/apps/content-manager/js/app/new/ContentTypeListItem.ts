module app_new {

    export class ContentTypeListItem {

        private siteRoot:boolean;

        private contentType:api_schema_content.ContentTypeSummary;

        constructor(contentType:api_schema_content.ContentTypeSummary, root?:boolean) {
            this.contentType = contentType;
            this.siteRoot = root || false;
        }

        getName() {
            return this.contentType.getName();
        }

        getDisplayName() {
            return this.contentType.getDisplayName();
        }

        getIconUrl() {
            return this.contentType.getIcon();
        }

        getContentType():api_schema_content.ContentTypeSummary {
            return this.contentType;
        }

        isSiteRoot() {
            return this.siteRoot;
        }
    }
}