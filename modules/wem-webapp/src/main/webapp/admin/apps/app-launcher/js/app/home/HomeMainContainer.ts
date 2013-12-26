module app_home {

    export class HomeMainContainer extends api_dom.DivEl {

        private brandingPanel: Branding;

        private appSelector: app_launcher.AppSelector;

        private loginForm: app_login.LoginForm;

        private appInfo: app_launcher.AppInfo;

        private linksContainer: LinksContainer;

        private centerPanel: CenterPanel;

        private backgroundImgUrl: string;

        constructor(builder: HomeMainContainerBuilder) {
            super(null, 'home-main-container');

            this.appSelector = builder.appSelector;
            this.loginForm = builder.loginForm;
            this.appInfo = builder.appInfo;
            this.linksContainer = builder.linksContainer;
            this.backgroundImgUrl = builder.backgroundImgUrl;

            var style = this.getHTMLElement().style;
            style.left = '0px';
            style.top = '0px';
            this.setBackgroundImgUrl(this.backgroundImgUrl);

            this.brandingPanel = new Branding();

            this.centerPanel = new CenterPanel();
            this.centerPanel.appendLeftColumn(this.appSelector);
            this.centerPanel.appendRightColumn(this.loginForm);
            this.centerPanel.appendRightColumn(this.appInfo);
            this.centerPanel.appendRightColumn(this.linksContainer);
            this.appendChild(this.brandingPanel);
            this.appendChild(this.centerPanel);
        }

        show() {
            super.show();
            this.appSelector.activateKeyBindings();
        }

        giveFocus() : boolean {
            return this.appSelector.giveFocus();
        }
    }

    export class HomeMainContainerBuilder {

        backgroundImgUrl: string;

        appSelector: app_launcher.AppSelector;

        loginForm: app_login.LoginForm;

        appInfo: app_launcher.AppInfo;

        linksContainer: app_home.LinksContainer;

        setBackgroundImgUrl(value: string): HomeMainContainerBuilder {
            this.backgroundImgUrl = value;
            return this;
        }

        setAppSelector(value: app_launcher.AppSelector): HomeMainContainerBuilder {
            this.appSelector = value;
            return this;
        }

        setLoginForm(value: app_login.LoginForm): HomeMainContainerBuilder {
            this.loginForm = value;
            return this;
        }

        setAppInfo(value: app_launcher.AppInfo): HomeMainContainerBuilder {
            this.appInfo = value;
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
