module app.home {

    export class HeaderPanel extends api.dom.DivEl {

        private installationHeader: api.dom.H1El;

        private returnButton: api.dom.DivEl;

        private returnAction: api.ui.Action;

        private userIcon: api.dom.ImgEl;

        private logoutEl: api.dom.AEl;

        private userName: api.dom.H2El;

        constructor() {
            super('header-panel');

            this.installationHeader = new api.dom.H1El("installation-header");

            this.returnButton = new api.dom.DivEl('return-button');
            this.returnButton.onClicked(() => {
                this.returnAction.execute()
            });
            this.returnButton.hide();

            this.userName = new api.dom.H2El("user-name");
            this.logoutEl = new api.dom.AEl("logout");
            this.logoutEl.setHtml("logout");
            this.logoutEl.hide();

            this.logoutEl.onClicked(() => {
                new LogOutEvent().fire()
            });


            this.appendChild(this.installationHeader);
            this.appendChild(this.returnButton);
            this.appendChild(this.logoutEl);
            this.appendChild(this.userName);

            app.home.LogInEvent.on((event) => {
                this.userName.setHtml(event.getUser().getDisplayName());
                this.logoutEl.show();
                //TODO: init icon for user
            });

            app.home.LogOutEvent.on((event) => {
                this.logoutEl.hide();
            });

            new api.system.StatusRequest().send().done((response: api.rest.JsonResponse<api.system.StatusJson>) => {
                var installationText = response.getResult().installation ? response.getResult().installation : "Enonic experience platform";
                this.installationHeader.setHtml(installationText);
            });

            this.onShown(() => {
                if (!this.userName) {
                    new api.security.auth.IsAuthenticatedRequest().sendAndParse().then((loginResult) => {
                        if (loginResult.isAuthenticated()) {
                            //TODO: init icon for user
                            this.userName.setHtml(loginResult.getUser().getDisplayName());
                            this.logoutEl.show();
                        }
                    });
                }
            });
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
