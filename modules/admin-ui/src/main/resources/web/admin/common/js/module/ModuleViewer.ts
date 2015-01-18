module api.module {

    export class ModuleViewer extends api.ui.Viewer<Module> {

        private namesAndIconView: api.app.NamesAndIconView;

        constructor() {
            super();
            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
            this.appendChild(this.namesAndIconView);
        }

        setObject(moduleObj: Module) {
            super.setObject(moduleObj);
            this.namesAndIconView.setMainName(moduleObj.getDisplayName()).
                setSubName(moduleObj.getName()).
                setIconClass("icon-puzzle icon-large");
        }

        getPreferredHeight(): number {
            return 50;
        }
    }
}