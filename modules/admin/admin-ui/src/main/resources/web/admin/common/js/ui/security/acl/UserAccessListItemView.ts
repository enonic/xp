module api.ui.security.acl {

    import Principal = api.security.Principal;
    import Tooltip = api.ui.Tooltip;
    import User = api.security.User;


    export class UserAccessListItemView extends api.ui.Viewer<UserAccessListItem> {

        private userLine: api.dom.DivEl;

        private accessLine: api.dom.DivEl;

        private resizeListener: (item: api.ui.responsive.ResponsiveItem) => void;

        private currentUser: User;

        private static OPTIONS: any[] = [
            {value: Access.FULL, name: 'Full Access'},
            {value: Access.PUBLISH, name: 'Can Publish'},
            {value: Access.WRITE, name: 'Can Write'},
            {value: Access.READ, name: 'Can Read'},
            {value: Access.CUSTOM, name: 'Custom...'}
        ];

        constructor(className?: string) {
            super('user-access-list-item-view' + (className ? " " + className : ""));
        }

        setCurrentUser(user: User) {
            this.currentUser = user;
        }

        doRender(): boolean {
            var data = <UserAccessListItem>this.getObject();

            this.accessLine = new api.dom.SpanEl("access-line").setHtml(UserAccessListItemView.OPTIONS[data.getAccess()].name);
            this.userLine = new api.dom.DivEl("user-line");

            var isEmpty: boolean = true;


            data.getPrincipals().forEach((principal: Principal) => {
                if (principal.isUser()) {
                    isEmpty = false;

                    var icon = new api.dom.SpanEl("user-icon").setHtml(principal.getDisplayName().substring(0, 2));
                    if (this.currentUser && this.currentUser.getKey().equals(principal.getKey())) {
                        icon.addClass("active");
                        this.userLine.insertChild(icon, 0);
                    } else {
                        this.userLine.appendChild(icon);
                    }
                    new Tooltip(icon, principal.getDisplayName(), 200).setMode(Tooltip.MODE_GLOBAL_STATIC);
                }
            });

            if (isEmpty) {
                return false;
            }

            this.appendChildren(this.accessLine, this.userLine);

            this.resizeListener = this.setExtraCount.bind(this);
            api.ui.responsive.ResponsiveManager.onAvailableSizeChanged(this, this.resizeListener);

            this.userLine.onRendered(() => {
                this.setExtraCount();
            })

            return true;
        }

        remove(): any {
            api.ui.responsive.ResponsiveManager.unAvailableSizeChanged(this);
            return super.remove();
        }

        private setExtraCount() {
            if (this.userLine.getChildren().length > 0) {
                var iconWidth = this.userLine.getChildren()[0].getEl().getWidthWithMargin(),
                    lineWidth = this.userLine.getEl().getWidthWithoutPadding(),
                    iconCount = this.userLine.getChildren().length;

                if (lineWidth >= (iconCount * iconWidth)) {
                    this.userLine.getEl().setAttribute("extra-count", "");
                } else {
                    var extraCount = Math.floor(((iconCount * iconWidth) - lineWidth) / iconWidth) + 1;
                    this.userLine.getEl().setAttribute("extra-count", "+" + extraCount);
                }
            }
        }


    }
}