module app.home {

    export class HomeMainContainer extends api.dom.DivEl {

        private brandingPanel: Branding;

        private appSelector: app.launcher.AppSelector;

        private loginForm: app.login.LoginForm;

        private linksContainer: LinksContainer;

        private centerPanel: CenterPanel;

        private backgroundImgUrl: string;

        private logoutButton: api.ui.button.Button;

        private returnButton: api.dom.DivEl;

        private returnAction: api.ui.Action;

        constructor(builder: HomeMainContainerBuilder) {
            super('home-main-container');

            this.getEl().setAttribute("tabindex", "100"); //Need tabindex to be able to focus element

            this.appSelector = builder.appSelector;
            this.loginForm = builder.loginForm;
            this.linksContainer = builder.linksContainer;
            this.backgroundImgUrl = builder.backgroundImgUrl;
            this.logoutButton = new api.ui.button.Button(_i18n('Sign out'));
            this.logoutButton.setClass("button logout-button");

            this.logoutButton.onClicked((event) => {
                new LogOutEvent().fire();
            });

            this.setBackgroundImgUrl(this.backgroundImgUrl);

            this.brandingPanel = new Branding();

            this.returnButton = new api.dom.DivEl('return-button');
            this.returnButton.hide();
            this.returnButton.onClicked(() => {
                this.returnAction.execute()
            });

            this.centerPanel = new CenterPanel();
            this.centerPanel.prependChild(this.returnButton);
            this.centerPanel.prependChild(this.brandingPanel);

            this.centerPanel.addToAppSelectorPanel(this.appSelector);
            this.centerPanel.addToAppSelectorPanel(this.logoutButton);

            this.centerPanel.addToLoginPanel(this.loginForm);
            this.centerPanel.addToLoginPanel(this.linksContainer);

            this.appendChild(this.centerPanel);

            LogOutEvent.on(() => {
                new api.security.auth.LogoutRequest().sendAndParse().then(() => {
                    api.util.CookieHelper.removeCookie('dummy.userIsLoggedIn');
                    this.centerPanel.showLoginPanel();
                    this.setBackgroundImgUrl(this.backgroundImgUrl);
                    this.returnButton.hide();
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).done();
            });

        }

        setReturnAction(action: api.ui.Action) {
            this.returnAction = action;
        }

        showLogin() {
            this.centerPanel.showLoginPanel();
        }

        showAppSelector() {
            this.centerPanel.showAppSelectorPanel();
        }

        show() {
            this.appSelector.showAppsCount();
            super.show();
        }

        hide() {
            api.ui.KeyBindings.get().unbindKeys(this.appSelector.getKeyBindings());
            super.hide();
        }

        enableReturnButton() {
            this.returnButton.show();
        }
    }

    export class HomeMainContainerBuilder {

        backgroundImgUrl: string;

        appSelector: app.launcher.AppSelector;

        loginForm: app.login.LoginForm;

        linksContainer: app.home.LinksContainer;

        setBackgroundImgUrl(value: string): HomeMainContainerBuilder {
            this.backgroundImgUrl = value;
            return this;
        }

        setAppSelector(value: app.launcher.AppSelector): HomeMainContainerBuilder {
            this.appSelector = value;
            return this;
        }

        setLoginForm(value: app.login.LoginForm): HomeMainContainerBuilder {
            this.loginForm = value;
            return this;
        }

        setLinksContainer(value: LinksContainer): HomeMainContainerBuilder {
            this.linksContainer = value;
            return this;
        }

        build(): HomeMainContainer {
            return new HomeMainContainer(this);
        }
    }

}
