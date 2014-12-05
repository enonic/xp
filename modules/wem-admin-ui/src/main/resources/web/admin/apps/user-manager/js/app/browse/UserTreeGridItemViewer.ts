module app.browse {

    export class UserTreeGridItemViewer extends api.ui.Viewer<UserTreeGridItem> {

        private namesAndIconView: api.app.NamesAndIconView;

        constructor() {
            super();
            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
            this.appendChild(this.namesAndIconView);
        }

        setObject(gridItem: UserTreeGridItem) {
            super.setObject(gridItem);
            this.namesAndIconView.setMainName(gridItem.getItemDisplayName());

            var itemType = gridItem.getType();
            if (itemType === UserTreeGridItemType.PRINCIPAL) {
                this.namesAndIconView.setSubName(gridItem.getPrincipal().getKey().getId());

            } else if (itemType === UserTreeGridItemType.USER_STORE) {
                this.namesAndIconView.setSubName('/' + gridItem.getUserStore().getKey().toString());
            } else {
                this.namesAndIconView.setSubName(gridItem.getItemDisplayName().toLocaleLowerCase());
            }

            this.selectIconClass(gridItem);
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
                    this.namesAndIconView.setIconClass("icon-shield icon-large");
                } else if (item.getPrincipal().isUser()) {
                    this.namesAndIconView.setIconClass("icon-user icon-large");
                } else if (item.getPrincipal().isGroup()) {
                    this.namesAndIconView.setIconClass("icon-users icon-large");
                }
                break;
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