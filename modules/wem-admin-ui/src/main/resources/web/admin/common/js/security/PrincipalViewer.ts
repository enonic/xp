module api.security {

    export class PrincipalViewer extends api.ui.Viewer<Principal> {

        private namesAndIconView: api.app.NamesAndIconView;

        constructor() {
            super();
            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
            this.appendChild(this.namesAndIconView);
        }

        setObject(principalObj: Principal) {
            super.setObject(principalObj);
            this.namesAndIconView.setMainName(principalObj.getDisplayName()).
                //setSubName(moduleObj.getName()).
                setIconClass("icon-puzzle icon-large");
        }

        getPreferredHeight(): number {
            return 50;
        }
    }
}