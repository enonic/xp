module app.view.detail {

    import CompareStatus = api.content.CompareStatus;
    import ContentSummary = api.content.ContentSummary;
    import Content = api.content.Content;
    import ContentId = api.content.ContentId;
    import CompareStatusFormatter = api.content.CompareStatusFormatter;
    import AccessControlList = api.security.acl.AccessControlList;
    import Access = api.security.acl.Access;
    import AccessControlEntry = api.security.acl.AccessControlEntry;
    import UserAccessListView = api.ui.security.acl.UserAccessListView;
    import UserAccessListItem = api.ui.security.acl.UserAccessListItem;
    import UserAccessListItemView = api.ui.security.acl.UserAccessListItemView;
    import Permission = api.security.acl.Permission;
    import PrincipalKey = api.security.PrincipalKey;
    import User = api.security.User;

    export class UserAccessWidgetItemView extends WidgetItemView {

        private contentId: ContentId;

        private accessListView: UserAccessListView;

        private headerEl: api.dom.SpanEl;

        private bottomEl;

        private currentUser: User;// TODO: need to implement caching for current user value;

        private static OPTIONS: any[] = [
            {value: Access.FULL, name: 'has full access to'},
            {value: Access.READ, name: 'Can Read'},
            {value: Access.WRITE, name: 'Can Write'},
            {value: Access.PUBLISH, name: 'Can Publish'},
            {value: Access.CUSTOM, name: 'has custom access to'}
        ];


        constructor() {
            super("user-access-widget-item-view");
            this.accessListView = new UserAccessListView();
        }

        public setContentId(content: ContentId) {
            this.contentId = content;
        }


        private layoutHeader(content: Content) {
            var entry = content.getPermissions().getEntry(api.security.RoleKeys.EVERYONE);
            if (entry) {
                var headerStr = entry.getPrincipalDisplayName() + " " + UserAccessWidgetItemView.OPTIONS[entry.getAccess()].name +
                                " this item";
                var headerStrEl = new api.dom.SpanEl("header-string").setHtml(headerStr);

                this.headerEl = new api.dom.DivEl("user-access-widget-header");

                this.headerEl.appendChild(new api.dom.DivEl("icon-menu4"));
                this.headerEl.appendChild(headerStrEl);
                this.appendChild(this.headerEl);
            }
        }

        private layoutBottom(content: Content) {

            this.bottomEl = new api.dom.AEl("edit-permissions-link").setHtml("Edit Permissions");
            this.appendChild(this.bottomEl);

            this.bottomEl.onClicked(() => {
                new api.content.OpenEditPermissionsDialogEvent(content).fire();
            });

        }

        private layoutList(content: Content) {
            var accessList = [];
            content.getPermissions().getEntries().map(
                (entry) => {
                    var access = entry.getAccess();
                    if (!accessList[access]) {
                        accessList[access] = [];
                    }
                    accessList[access].push(entry.getPrincipal());
                }
            );

            var userAccessList: UserAccessListItemView[] = [];

            for (var key in accessList) {
                var listItem = new UserAccessListItem(key, accessList[key]);
                var listItemView = new UserAccessListItemView();
                listItemView.setCurrentUser(this.currentUser);

                listItemView.setObject(listItem);

                userAccessList.push(listItemView);
            }

            this.accessListView.setItemViews(userAccessList);

            this.appendChild(this.accessListView);
        }

        public doRender(): boolean {
            super.doRender();

            new api.security.auth.IsAuthenticatedRequest().sendAndParse().then((loginResult) => {

                this.currentUser = loginResult.getUser();

                new api.content.GetContentByIdRequest(this.contentId).sendAndParse().
                    then((content: Content) => {
                        if (content) {
                            this.layoutHeader(content);
                            this.layoutList(content);
                            if (content.isAnyPrincipalAllowed(loginResult.getPrincipals(),
                                    api.security.acl.Permission.WRITE_PERMISSIONS)) {
                                this.layoutBottom(content);
                            }
                        }

                    }).done();

            });

            return true;
        }


    }
}