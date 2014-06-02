module app.create {

    import ContentTypeSummary = api.schema.content.ContentTypeSummary;
    import SiteTemplateSummary = api.content.site.template.SiteTemplateSummary;

    export class NewContentDialogListItem {

        private contentType: ContentTypeSummary;
        private siteTemplate: SiteTemplateSummary;

        private name: string;
        private displayName: string;
        private iconUrl: string;

        static fromContentType(contentType: ContentTypeSummary): NewContentDialogListItem {
            return new NewContentDialogListItem(contentType, null);
        }

        static fromSiteTemplate(siteTemplate: SiteTemplateSummary, rootContentType: ContentTypeSummary): NewContentDialogListItem {
            return new NewContentDialogListItem(rootContentType, siteTemplate);
        }

        constructor(contentType: ContentTypeSummary, siteTemplate: SiteTemplateSummary) {
            this.contentType = contentType;
            this.siteTemplate = siteTemplate;

            if (siteTemplate) {
                api.util.assert(siteTemplate.getRootContentType().equals(contentType.getContentTypeName()),
                    "Content type " + contentType.getName() + " must be equal to site template root content type " + siteTemplate.getRootContentType().toString());
            }

            this.name = siteTemplate ? siteTemplate.getName() : contentType.getName();
            this.displayName = siteTemplate ? siteTemplate.getDisplayName() : contentType.getDisplayName();
            this.iconUrl = siteTemplate ? siteTemplate.getIconUrl() : contentType.getIconUrl();
        }

        getContentType(): ContentTypeSummary {
            return this.contentType;
        }

        getSiteTemplate(): SiteTemplateSummary {
            return this.siteTemplate;
        }

        getName(): string {
            return this.name;
        }

        getDisplayName(): string {
            return this.displayName;
        }

        getIconUrl(): string {
            return this.iconUrl;
        }

        isSiteTemplate(): boolean {
            return !!this.siteTemplate;
        }
    }
}