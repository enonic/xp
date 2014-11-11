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
                    this.namesAndIconView.setIconClass("icon-users2 icon-large");
                    break;
                }
                if (item.getPrincipal().isUser()) {
                    this.namesAndIconView.setIconClass("icon-user icon-large");
                    break;
                }
                if (item.getPrincipal().isGroup()) {
                    this.namesAndIconView.setIconClass("icon-users icon-large");
                    break;
                }

            }
            case UserTreeGridItemType.GROUPS:
            {
                this.namesAndIconView.setIconClass("icon-folder icon-large");
                break;
            }
            case UserTreeGridItemType.ROLES:
            {
                this.namesAndIconView.setIconClass("icon-folder icon-large");
                break;
            }
            case UserTreeGridItemType.USERS:
            {
                this.namesAndIconView.setIconClass("icon-folder icon-large");
                break;
            }
            }

        }

        getPreferredHeight(): number {
            return 50;
        }
    }
}