module app.browse {

    export class TemplateSummaryViewer extends api.ui.Viewer<TemplateSummary> {

        private namesAndIconView: api.app.NamesAndIconView;

        private templateIconUrlResolver: TemplateIconUrlResolver;

        constructor() {
            super();
            this.templateIconUrlResolver = new TemplateIconUrlResolver();

            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
            this.appendChild(this.namesAndIconView);
        }

        setObject(templateSummary: TemplateSummary) {
            super.setObject(templateSummary);

            this.templateIconUrlResolver.setTemplate(templateSummary);
            var iconUrl = this.templateIconUrlResolver.resolve();

            this.namesAndIconView.setMainName(templateSummary.getDisplayName()).
                setSubName(templateSummary.getName()).
                setIconUrl(iconUrl);
        }

        getPreferredHeight(): number {
            return 50;
        }
    }
}