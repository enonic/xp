module app.home {

    export class HomeMainContainer extends api.dom.DivEl {

        private brandingPanel: Branding;

        private appSelector: app.launcher.AppSelector;

        private loginForm: app.login.LoginForm;

        private linksContainer: LinksContainer;

        private centerPanel: CenterPanel;

        private backgroundImgUrl: string;

        private logoutButton:api.ui.Button;

        constructor(builder: HomeMainContainerBuilder) {
            super('home-main-container');

            this.appSelector = builder.appSelector;
            this.loginForm = builder.loginForm;
            this.linksContainer = builder.linksContainer;
            this.backgroundImgUrl = builder.backgroundImgUrl;
            this.logoutButton = new api.ui.Button(_i18n('Sign out'));
            this.logoutButton.setClass("button logout-button");

            this.logoutButton.onClicked((event) => {
                api.util.CookieHelper.removeCookie('dummy.userIsLoggedIn');
                this.centerPanel.showLoginPanel();
            });

            this.setBackgroundImgUrl(this.backgroundImgUrl);

            this.brandingPanel = new Branding();

            this.centerPanel = new CenterPanel();
            this.centerPanel.prependChild(this.brandingPanel);

            this.centerPanel.addToAppSelectorPanel(this.appSelector);
            this.centerPanel.addToAppSelectorPanel(this.logoutButton);

            this.centerPanel.addToLoginPanel(this.loginForm);
            this.centerPanel.addToLoginPanel(this.linksContainer);

            this.appendChild(this.centerPanel);

            api.app.ShowAppLauncherEvent.on((event) => {
                this.setBackgroundImgUrl("");
            });
        }

        giveFocus(): boolean {
            return this.appSelector.giveFocus();
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
