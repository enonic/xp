module app_new {

    export class ContentTypeListItem {

        private siteRoot: boolean;

        private contentType: api_schema_content.ContentTypeSummary;

        private iconUrl: string;

        constructor(contentType: api_schema_content.ContentTypeSummary, root?: boolean) {
            this.contentType = contentType;
            this.siteRoot = root || false;
            this.iconUrl = contentType.getIconUrl();
        }

        getName(): string {
            return this.contentType.getName();
        }

        getDisplayName(): string {
            return this.contentType.getDisplayName();
        }

        getIconUrl():string {
            return this.iconUrl;
        }

        getContentType(): api_schema_content.ContentTypeSummary {
            return this.contentType;
        }

        isSiteRoot():boolean {
            return this.siteRoot;
        }
    }
}