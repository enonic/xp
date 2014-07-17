module api.content.site.template {

    export class SiteTemplateSummaryViewer extends api.ui.Viewer<SiteTemplateSummary> {

        private namesAndIconView: api.app.NamesAndIconView;

        constructor() {
            super();
            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
            this.namesAndIconView.setIconClass("icon-earth icon-large");
            this.appendChild(this.namesAndIconView);
        }

        setObject(siteTemplate: SiteTemplateSummary) {
            super.setObject(siteTemplate);
            this.namesAndIconView.setMainName(siteTemplate.getDisplayName()).
                setSubName(siteTemplate.getDescription());
        }

        getPreferredHeight(): number {
            return 50;
        }
    }
}