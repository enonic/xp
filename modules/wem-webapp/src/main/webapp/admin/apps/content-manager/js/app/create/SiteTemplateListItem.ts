module app.create {

    export class SiteTemplateListItem extends api.dom.LiEl {

        private siteTemplate: api.content.site.template.SiteTemplateSummary;

        private contentType: api.schema.content.ContentTypeSummary;

        constructor(item: api.content.site.template.SiteTemplateSummary, contentType: api.schema.content.ContentTypeSummary) {
            super("site site-template-list-item");

            this.siteTemplate = item;
            this.contentType = contentType;

            var namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize( api.app.NamesAndIconViewSize.small ).build();

            namesAndIconView
                .setIconUrl(contentType.getIconUrl())
                .setMainName(item.getDisplayName())
                .setSubName(item.getName())
                .setDisplayIconLabel(true);

            this.appendChild(namesAndIconView);
        }

        getName(): string {
            return this.siteTemplate.getName();
        }

        getDisplayName(): string {
            return this.siteTemplate.getDisplayName();
        }

        getIconUrl(): string {
            return this.contentType.getIconUrl();
        }

        getSiteTemplate(): api.content.site.template.SiteTemplateSummary {
            return this.siteTemplate;
        }

        getContentType(): api.schema.content.ContentTypeSummary {
            return this.contentType;
        }

    }
}