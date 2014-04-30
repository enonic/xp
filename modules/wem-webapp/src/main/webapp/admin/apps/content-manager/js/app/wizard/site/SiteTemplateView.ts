module app.wizard.site {

    import ContentType = api.schema.content.ContentType;
    import SiteTemplate = api.content.site.template.SiteTemplate;

    export class SiteTemplateView extends api.app.NamesAndIconView {

        private contentType: ContentType;

        private siteTemplate: SiteTemplate;

        constructor(contentType: ContentType) {
            super(new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.medium));
            this.addClass('template-view');
            this.contentType = contentType;
        }

        setValue(siteTemplate: api.content.site.template.SiteTemplate) {
            this.siteTemplate = siteTemplate;

            this.setMainName(siteTemplate.getDisplayName()).
                setSubName(siteTemplate.getDescription()).
                setIconUrl(this.contentType.getIconUrl());
        }
    }

}