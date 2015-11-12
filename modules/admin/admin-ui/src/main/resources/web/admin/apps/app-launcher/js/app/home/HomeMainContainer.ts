module app.home {

    export class HomeMainContainer extends api.dom.DivEl {

        private brandingPanel: Branding;

        private appSelector: app.launcher.AppSelector;

        private loginForm: app.login.LoginForm;

        private linksContainer: LinksContainer;

        private centerPanel: CenterPanel;

        private headerPanel: HeaderPanel;

        constructor(builder: HomeMainContainerBuilder) {
            super('home-main-container');

            var lazyImage = new api.ui.image.LazyImage(api.util.UriHelper.getAdminUri("/common/images/image1x1.png"));
            this.appendChild(lazyImage);

            this.getEl().setAttribute("tabindex", "100"); //Need tabindex to be able to focus element

            this.appSelector = builder.appSelector;
            this.loginForm = builder.loginForm;
            this.linksContainer = builder.linksContainer;

            this.headerPanel = new HeaderPanel();
            this.headerPanel.hide();

            this.brandingPanel = new Branding();

            this.centerPanel = new CenterPanel();
            this.centerPanel.prependChild(this.headerPanel);
            this.centerPanel.prependChild(this.brandingPanel);

            this.centerPanel.addToAppSelectorPanel(this.appSelector);

            this.centerPanel.addToLoginPanel(this.loginForm);
            this.centerPanel.addToLoginPanel(this.linksContainer);

            this.appendChild(this.centerPanel);

            this.onAdded(() => {
                lazyImage.setSrc("/admin/common/images/background-1920.jpg");
            });

            LogOutEvent.on(() => {
                new api.security.auth.LogoutRequest().sendAndParse().then(() => {
                    this.centerPanel.showLoginPanel();
                    this.headerPanel.hide();
                    this.brandingPanel.show();
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).done();
            });

            LogInEvent.on(() => {
                this.headerPanel.show();
                this.disableBranding();
            });

            this.showBrowserWarningMessage();

        }

        setReturnAction(action: api.ui.Action) {
            this.headerPanel.setReturnAction(action);
        }

        showLogin() {
            this.centerPanel.showLoginPanel();
            this.brandingPanel.show();
            this.headerPanel.hide();
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
            this.headerPanel.enableReturnButton();
        }

        disableBranding() {
            this.brandingPanel.hide();
        }

        private showBrowserWarningMessage() {
            if (!api.BrowserHelper.isAvailableBrowser()) {
                if (api.BrowserHelper.isOldBrowser()) {
                    api.notify.showError("Your browser version is obsolete. Please upgrade", false);
                } else {
                    api.notify.showError("Your browser currently is not supported, try Firefox or Chrome", false);
                }
            }
        }
    }

    export class HomeMainContainerBuilder {

        appSelector: app.launcher.AppSelector;

        loginForm: app.login.LoginForm;

        linksContainer: app.home.LinksContainer;

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
