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
            this.selectIconClass(userItem);

        }

        private selectIconClass(item: UserTreeGridItem) {
            var type: UserTreeGridItemType = item.getType();
            switch (type) {
            case UserTreeGridItemType.USER_STORE:
            {
                this.namesAndIconView.setIconClass("icon-address-book icon-large");
                break;
            }
            case UserTreeGridItemType.PRINCIPAL:
            {
                if (item.getPrincipal().isRole()) {
                    //TODO need to specify correct class
                    this.namesAndIconView.setIconClass("icon-users icon-large");
                    break;
                }
                if (item.getPrincipal().isUser()) {
                    //TODO need to specify correct class
                    this.namesAndIconView.setIconClass("icon-users icon-large");
                    break;
                }
                if (item.getPrincipal().isGroup()) {
                    this.namesAndIconView.setIconClass("icon-users icon-large");
                    break;
                }

            }
            case UserTreeGridItemType.GROUPS:
            {        //TODO need to specify correct class
                this.namesAndIconView.setIconClass("icon-users icon-large");
                break;
            }
            case UserTreeGridItemType.ROLES:

            {       //TODO need to specify correct class
                this.namesAndIconView.setIconClass("icon-users icon-large");
                break;
            }
            case UserTreeGridItemType.USERS:
            {       //TODO need to specify correct class
                this.namesAndIconView.setIconClass("icon-users icon-large");
                break;
            }
            }

        }

        getPreferredHeight(): number {
            return 50;
        }
    }
}