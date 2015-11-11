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
    import UserAccessListItemView = api.ui.security.acl.UserAccessListItemView;
    import Permission = api.security.acl.Permission;
    import Principal = api.security.Principal;
    import PrincipalKey = api.security.PrincipalKey;
    import User = api.security.User;

    export class UserAccessWidgetItemView extends WidgetItemView {

        private contentId: ContentId;

        private accessListView: UserAccessListView;

        private headerEl: api.dom.SpanEl;

        private bottomEl;

        private currentUser: User;// TODO: need to implement caching for current user value;

        private everyoneAccessValue: Access;

        public static debug = false;

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

        public setContentId(contentId: ContentId): wemQ.Promise<any> {
            if (UserAccessWidgetItemView.debug) {
                console.debug('UserAccessWidgetItemView.setContentId: ', contentId);
            }
            this.contentId = contentId;
            return this.layout();
        }


        private layoutHeader(content: Content) {
            var entry = content.getPermissions().getEntry(api.security.RoleKeys.EVERYONE);
            this.everyoneAccessValue = null;
            if (entry) {

                this.everyoneAccessValue = AccessControlEntryView.getAccessValueFromEntry(entry);
                var headerStr = entry.getPrincipalDisplayName() + " " + this.getOptionName(this.everyoneAccessValue) +
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

            var request = new api.content.GetEffectivePermissions(content.getContentId());

            request.sendAndParse().then((results: api.ui.security.acl.EffectivePermission[]) => {

                var userAccessList = this.getUserAccessList(results);

                this.accessListView = new UserAccessListView();
                this.accessListView.setItemViews(userAccessList);
                this.appendChild(this.accessListView);

                deferred.resolve(true);
            }).done();

            return deferred.promise;

        }

        public layout(): wemQ.Promise<any> {
            if (UserAccessWidgetItemView.debug) {
                console.debug('UserAccessWidgetItemView.layout');
            }
            this.removeChildren();

            return super.layout().then(this.layoutUserAccess.bind(this));
        }

        private layoutUserAccess(): wemQ.Promise<any> {
            return new api.security.auth.IsAuthenticatedRequest().sendAndParse().then((loginResult) => {

                this.currentUser = loginResult.getUser();
                if (this.contentId) {
                    return new api.content.GetContentByIdRequest(this.contentId).sendAndParse().
                        then((content: Content) => {

                            if (content) {
                                this.layoutHeader(content);
                                return this.layoutList(content).then(() => {
                                    if (content.isAnyPrincipalAllowed(loginResult.getPrincipals(),
                                            api.security.acl.Permission.WRITE_PERMISSIONS)) {

                                        this.layoutBottom(content);
                                    }
                                });
                            }
                        });
                }
            });
        }

        private getUserAccessList(results: api.ui.security.acl.EffectivePermission[]): UserAccessListItemView[] {

            return results.
                filter(item => item.getAccess() != this.everyoneAccessValue).
                map((item: api.ui.security.acl.EffectivePermission) => {
                    var view = new UserAccessListItemView();
                    view.setObject(item);
                    view.setCurrentUser(this.currentUser);
                    return view;
                });
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