module app.browse {

    export class UserTreeGridItemViewer extends api.ui.Viewer<UserTreeGridItem> {

        private namesAndIconView: api.app.NamesAndIconView;

        constructor() {
            super();
            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
            this.appendChild(this.namesAndIconView);
        }

        setObject(userItem: UserTreeGridItem) {
            super.setObject(userItem);
            this.namesAndIconView.setMainName(userItem.getItemDisplayName());
            switch (userItem.getType()) {
            case UserTreeGridItemType.USER_STORE:
            {
                this.namesAndIconView.setIconClass("icon-puzzle icon-large"); // icon-notebook
            }
            case UserTreeGridItemType.PRINCIPAL:
            {
                this.namesAndIconView.setIconClass("icon-puzzle icon-large"); // icon-users
            }
            case UserTreeGridItemType.GROUPS:
            {
                this.namesAndIconView.setIconClass("icon-puzzle icon-large");
            }
            case UserTreeGridItemType.ROLES:
            {
                this.namesAndIconView.setIconClass("icon-puzzle icon-large");
            }
            }
        }

        getPreferredHeight(): number {
            return 50;
        }
    }
}