module app.wizard.site {

    export class TemplateView extends api.app.NamesAndIconView {

        private contentType: api.schema.content.ContentType;

        private siteTemplate: api.content.site.template.SiteTemplate;

        constructor() {
            super(new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.medium));
            this.addClass('template-view');
        }

        setValue(siteTemplate: api.content.site.template.SiteTemplate, contentType: api.schema.content.ContentType) {
            this.siteTemplate = siteTemplate;
            this.contentType = contentType;

            this.setMainName(siteTemplate.getDisplayName()).
                setSubName(siteTemplate.getDescription()).
                setIconUrl(this.contentType.getIconUrl());
        }

        getSiteTemplateKey(): api.content.site.template.SiteTemplateKey {
            return this.siteTemplate.getKey();
        }
    }

}