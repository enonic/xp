module app.view.detail {

    import CompareStatus = api.content.CompareStatus;
    import ContentSummary = api.content.ContentSummary;
    import Content = api.content.Content;
    import ContentId = api.content.ContentId;
    import CompareStatusFormatter = api.content.CompareStatusFormatter;
    import AccessControlList = api.security.acl.AccessControlList;
    import Access = api.ui.security.acl.Access;
    import AccessControlEntry = api.security.acl.AccessControlEntry;
    import AccessControlEntryView = api.ui.security.acl.AccessControlEntryView;
    import UserAccessListView = api.ui.security.acl.UserAccessListView;
    import UserAccessListItem = api.ui.security.acl.UserAccessListItem;
    import UserAccessListItemView = api.ui.security.acl.UserAccessListItemView;
    import Permission = api.security.acl.Permission;
    import ResolveMembersRequest = api.security.ResolveMembersRequest;
    import ResolveMemberResult = api.security.ResolveMemberResult;
    import ResolveMembersResult = api.security.ResolveMembersResult;
    import Principal = api.security.Principal;
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
            {value: Access.PUBLISH, name: 'can publish'},
            {value: Access.WRITE, name: 'can write'},
            {value: Access.READ, name: 'can read'},
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

                var headerStr = entry.getPrincipalDisplayName() + " " +
                                                                      this.getOptionName(AccessControlEntryView.getAccessValueFromEntry(entry)) +
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

        private layoutList(content: Content): wemQ.Promise<boolean> {

            var deferred = wemQ.defer<boolean>();


            var accessUsersMap = [],
                request = new ResolveMembersRequest();

            content.getPermissions().getEntries().map(
                (entry) => {

                    var access = AccessControlEntryView.getAccessValueFromEntry(entry);
                    if (!accessUsersMap[access]) {
                        accessUsersMap[access] = [];
                    }
                    accessUsersMap[access].push(entry.getPrincipal());
                    if (entry.getPrincipal().isGroup() || entry.getPrincipal().isRole()) {
                        request.addKey(entry.getPrincipalKey());
                    }
                }
            );

            request.sendAndParse().then((results: ResolveMembersResult) => {

                var userAccessList = this.getUserAccessList(accessUsersMap, results);

                this.accessListView.setItemViews(userAccessList);
                this.appendChild(this.accessListView);

                deferred.resolve(true);
            }).done();

            return deferred.promise;

        }

        public doRender(): boolean {
            super.doRender();

            new api.security.auth.IsAuthenticatedRequest().sendAndParse().then((loginResult) => {

                this.currentUser = loginResult.getUser();

                new api.content.GetContentByIdRequest(this.contentId).sendAndParse().
                    then((content: Content) => {
                        if (content) {
                            this.layoutHeader(content);
                            this.layoutList(content).then(() => {
                                if (content.isAnyPrincipalAllowed(loginResult.getPrincipals(),
                                        api.security.acl.Permission.WRITE_PERMISSIONS)) {
                                    this.layoutBottom(content);
                                }
                            });
                        }

                    }).done();

            });

            return true;
        }

        private getUserAccessList(accessUsersMap, results: ResolveMembersResult): UserAccessListItemView[] {

            var keys = results.getValues().map((entry: ResolveMemberResult) => entry.getPrincipalKey().toString()),
                uniqueKeys: string[] = [],
                userAccessList: UserAccessListItemView[] = [];

            for (var key in accessUsersMap) {

                var listItem = new UserAccessListItem(key);

                accessUsersMap[key].forEach((principal: api.security.Principal) => {
                    var members = [];
                    if (keys.indexOf(principal.getKey().toString()) > -1) {
                        members = results.getByPrincipalKey(principal.getKey()).getMembers();
                    } else {
                        members = accessUsersMap[key];
                    }

                    members = members.filter((member) => {
                        return uniqueKeys.indexOf(member.getKey().toString()) == -1;
                    });

                    listItem.addItems(members);

                    uniqueKeys = uniqueKeys.concat(members.map((curPrincipal: Principal) => curPrincipal.getKey().toString()));
                });

                var listItemView = new UserAccessListItemView();
                listItemView.setCurrentUser(this.currentUser);

                listItemView.setObject(listItem);

                userAccessList.push(listItemView);
            }

            return userAccessList;
        }

        private getOptionName(access: Access): string {
            var currentOption = UserAccessWidgetItemView.OPTIONS.filter(option => {
                return option.value == access;
            });
            if (currentOption && currentOption.length > 0) {
                return currentOption[0].name;
            }
        }
    }
}