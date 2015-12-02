module api.ui.security.acl {

    import Permission = api.security.acl.Permission;
    import ContentId = api.content.ContentId;
    import Content = api.content.Content;
    import Principal = api.security.Principal;
    import PrincipalType = api.security.PrincipalType;
    import AccessControlEntry = api.security.acl.AccessControlEntry;

    export class UserAccessListView extends api.ui.selector.list.ListBox<AccessControlEntry> {

        private userAccessListItemViews: UserAccessListItemView[];


        constructor(className?: string) {
            super('user-access-list-view' + (className ? " " + className : ""));
        }

        doRender(): boolean {
            super.doRender();

            if (this.userAccessListItemViews && this.userAccessListItemViews.length > 0) {
                this.userAccessListItemViews.forEach((userAccessListItemView: UserAccessListItemView) => {
                    this.appendChild(userAccessListItemView);
                });
            }
            return true;
        }

        setItemViews(userAccessListItemViews: UserAccessListItemView[]) {
            this.userAccessListItemViews = userAccessListItemViews;
        }

    }

}