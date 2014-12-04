module app.view {

    import ViewItem = api.app.view.ViewItem;
    import ItemStatisticsPanel = api.app.view.ItemStatisticsPanel;
    import ItemDataGroup = api.app.view.ItemDataGroup;

    import UserTreeGridItem = app.browse.UserTreeGridItem;
    import UserTreeGridItemType = app.browse.UserTreeGridItemType;

    import Principal = api.security.Principal;
    import PrincipalType = api.security.PrincipalType;
    import GetPrincipalByKeyRequest = api.security.GetPrincipalByKeyRequest;

    import PrincipalViewer = api.ui.security.PrincipalViewer;


    export class UserItemStatisticsPanel extends ItemStatisticsPanel<UserTreeGridItem> {

        private userDataContainer: api.dom.DivEl;

        constructor() {
            super("principal-item-statistics-panel");

            this.userDataContainer = new api.dom.DivEl("user-data-container");
            this.appendChild(this.userDataContainer);
        }

        setItem(item: ViewItem<UserTreeGridItem>) {
            var currentItem = this.getItem();

            if (!currentItem || !currentItem.equals(item)) {

                switch (item.getModel().getType()) {
                case UserTreeGridItemType.PRINCIPAL:
                    this.populatePrincipalViewItem(item);
                    break;
                default:

                }

                this.userDataContainer.removeChildren();
                var type = !!item.getModel().getPrincipal() ? item.getModel().getPrincipal().getType() : -1;
                switch (type) {
                case PrincipalType.USER:
                    this.appendUserMetadata(item);
                    break;
                case PrincipalType.GROUP:
                    break;
                case PrincipalType.ROLE:
                    break;
                }

                super.setItem(item);
            }
        }

        private populatePrincipalViewItem(item: ViewItem<UserTreeGridItem>) {
            var type = !!item.getModel().getPrincipal() ? item.getModel().getPrincipal().getType() : -1;
            switch (type) {
                case PrincipalType.USER:
                    item.setPathName(item.getModel().getPrincipal().getKey().getId());
                    item.setPath(item.getModel().getDataPath());
                    item.setIconSize(128);
                    break;
                case PrincipalType.GROUP:
                    break;
                case PrincipalType.ROLE:
                    break;
            }

        }

        private appendUserMetadata(item: ViewItem<UserTreeGridItem>) {
            // Insert an empty data first to avoid blinking, after full data is loaded.
            var userGroup = new ItemDataGroup("User", "user");
            userGroup.addDataList("E-mail", " ");
            this.userDataContainer.appendChild(userGroup);

            var rolesAndGroupsGroup = new ItemDataGroup("Roles & Groups", "roles-and-groups");
            rolesAndGroupsGroup.addDataArray("Roles", []);
            rolesAndGroupsGroup.addDataArray("Groups", []);
            this.userDataContainer.appendChild(rolesAndGroupsGroup);

            new GetPrincipalByKeyRequest(item.getModel().getPrincipal().getKey()).
                includeUserMemberships(true).
                sendAndParse().
                then((principal: Principal) => {
                    userGroup = new ItemDataGroup("User", "user");
                    userGroup.addDataList("E-mail", principal.asUser().getEmail());

                    rolesAndGroupsGroup = new ItemDataGroup("Roles & Groups", "roles-and-groups");

                    var roles = principal.asUser().getMemberships().
                        filter((el) => { return el.isRole()}).
                        map((el) => {
                            var viewer = new PrincipalViewer();
                            viewer.setObject(el);
                            return viewer.getHtml();
                        });
                    rolesAndGroupsGroup.addDataArray("Roles", roles);

                    var groups = principal.asUser().getMemberships().
                        filter((el) => { return el.isGroup()}).
                        map((el) => {
                            var viewer = new PrincipalViewer();
                            viewer.setObject(el);
                            return viewer.getHtml();
                        });
                    rolesAndGroupsGroup.addDataArray("Groups", groups);

                    this.userDataContainer.removeChildren();
                    this.userDataContainer.appendChild(userGroup);
                    this.userDataContainer.appendChild(rolesAndGroupsGroup);
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).finally(() => {
                }).done();
        }
    }
}
