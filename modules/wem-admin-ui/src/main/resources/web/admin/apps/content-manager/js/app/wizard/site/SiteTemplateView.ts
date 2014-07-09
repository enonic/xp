module app.wizard.site {

    import SiteTemplate = api.content.site.template.SiteTemplate;

    export class SiteTemplateView extends api.app.NamesAndIconView {

        private siteTemplate: SiteTemplate;

        constructor() {
            super(new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.medium));
            this.addClass('template-view');
        }

        setValue(siteTemplate: api.content.site.template.SiteTemplate) {
            this.siteTemplate = siteTemplate;

            this.setMainName(siteTemplate.getDisplayName()).
                setSubName(siteTemplate.getDescription()).
                setIconUrl(this.siteTemplate.getIconUrl());
        }
    }

}