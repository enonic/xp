module api.module {

    export class ModuleSummaryViewer extends api.ui.Viewer<ModuleSummary> {

        private namesAndIconView: api.app.NamesAndIconView;

        constructor() {
            super();
            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
            this.appendChild(this.namesAndIconView);
        }

        setObject(moduleSummary: ModuleSummary) {
            super.setObject(moduleSummary);
            this.namesAndIconView.setMainName(moduleSummary.getDisplayName()).
                setSubName(moduleSummary.getName()).
                setIconClass("icon-puzzle icon-large");
        }

        getPreferredHeight(): number {
            return 50;
        }
    }
}