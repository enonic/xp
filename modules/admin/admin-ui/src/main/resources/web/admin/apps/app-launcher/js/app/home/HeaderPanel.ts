module app.home {

    import TabMenuItemBuilder = api.ui.tab.TabMenuItemBuilder;

    export class HeaderPanel extends api.dom.DivEl {

        private installationHeader: api.dom.H1El;

        private returnButton: api.dom.DivEl;

        private returnAction: api.ui.Action;

        private userIcon: api.dom.ImgEl;

        private logonMenu: api.ui.tab.TabMenu;

        constructor() {
            super('header-panel');

            this.installationHeader = new api.dom.H1El("installation-header");

            this.returnButton = new api.dom.DivEl('return-button');
            this.returnButton.onClicked(() => {
                this.returnAction.execute()
            });
            this.returnButton.hide();

            this.logonMenu = new api.ui.tab.TabMenu("tab-menu-logon");
            this.logonMenu.hide();
            var logoutMenuItem = (<TabMenuItemBuilder>new TabMenuItemBuilder().setLabel("Log out")).build();
            logoutMenuItem.onClicked(() => {
                new LogOutEvent().fire()
            });
            this.logonMenu.addNavigationItem(logoutMenuItem);

            this.appendChild(this.installationHeader);
            this.appendChild(this.returnButton);
            this.appendChild(this.logonMenu);

            app.home.LogInEvent.on((event) => {
                this.logonMenu.setButtonLabel(event.getUser().getDisplayName());
                this.logonMenu.show();
                //TODO: init icon for user
            });

            app.home.LogOutEvent.on((event) => {
                this.logonMenu.hide();
            });

            new api.system.StatusRequest().sendAndParse().then((status: api.system.StatusResult) => {
                var installationText = status.getInstallation() || "Enonic experience platform";
                this.installationHeader.setHtml(installationText);
            }).done();

        }

        enableReturnButton() {
            this.returnButton.show();
        }

        disableReturnButton() {
            this.returnButton.hide();
        }


        setReturnAction(action: api.ui.Action) {
            this.returnAction = action;
        }


    }

}
