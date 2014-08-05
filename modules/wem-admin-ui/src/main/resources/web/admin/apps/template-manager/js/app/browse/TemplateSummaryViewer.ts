module app.browse {

    import TemplateSummary = api.content.TemplateSummary;

    export class TemplateSummaryViewer extends api.ui.Viewer<TemplateSummary> {

        private namesAndIconView: api.app.NamesAndIconView;

        private pageTemplateIconUri: string;

        private siteTemplateIconUri: string;

        constructor() {
            super();
            this.pageTemplateIconUri = api.util.getAdminUri('common/images/icons/icoMoon/32x32/newspaper.png');
            this.siteTemplateIconUri = api.util.getAdminUri('common/images/icons/icoMoon/32x32/earth.png');

            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
            this.appendChild(this.namesAndIconView);
        }

        setObject(templateSummary: TemplateSummary) {
            super.setObject(templateSummary);

            var iconUrl;
            if (templateSummary.isPageTemplate()) {
                iconUrl = this.pageTemplateIconUri;
            } else {
                iconUrl = templateSummary.getIconUrl() || this.siteTemplateIconUri;
            }

            this.namesAndIconView.setMainName(templateSummary.getDisplayName()).
                setSubName(templateSummary.getName()).
                setIconUrl(iconUrl);
        }

        getPreferredHeight(): number {
            return 50;
        }
    }
}