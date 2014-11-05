module api.security {

    export class UserTreeGridItemViewer extends api.ui.Viewer<UserTreeGridItem> {

        private namesAndIconView: api.app.NamesAndIconView;

        constructor() {
            super();
            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
            this.appendChild(this.namesAndIconView);
        }

        setObject(userObj: UserTreeGridItem) {
            super.setObject(userObj);
            this.namesAndIconView.setMainName(userObj.getDisplayName()).
                //setSubName(principalObj.getKey().getId()).
                setIconClass("icon-puzzle icon-large");
        }

        getPreferredHeight(): number {
            return 50;
        }
    }
}