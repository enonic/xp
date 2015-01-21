module app.browse {

    export class UserTreeGridItemViewer extends api.ui.Viewer<UserTreeGridItem> {

        private namesAndIconView: api.app.NamesAndIconView;

        constructor() {
            super();
            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
            this.appendChild(this.namesAndIconView);
        }

        setObject(gridItem:UserTreeGridItem, relativePath:boolean = false) {
            super.setObject(gridItem);
            this.namesAndIconView.setMainName(gridItem.getItemDisplayName());
            this.namesAndIconView.setSubName(this.resolveSubName(gridItem, relativePath));

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

        private resolveSubName(gridItem:UserTreeGridItem, relativePath:boolean):string {
            var itemType = gridItem.getType();
            switch (itemType) {
                case UserTreeGridItemType.USER_STORE:
                {
                    return ('/' + gridItem.getUserStore().getKey().toString());
                }
                case UserTreeGridItemType.PRINCIPAL:
                {
                    if (relativePath) {
                        return gridItem.getPrincipal().getKey().getId();
                    } else {
                        return gridItem.getPrincipal().getKey().toPath();
                    }
                }
                default:
                {
                    return gridItem.getItemDisplayName().toLocaleLowerCase();
                }
            }
        }

        getPreferredHeight(): number {
            return 50;
        }
    }
}