module app.home {

    export class HomeMainContainer extends api.dom.DivEl {

        private brandingPanel: Branding;

        private appSelector: app.launcher.AppSelector;

        private loginForm: app.login.LoginForm;

        private appInfo: app.launcher.AppInfo;

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

        giveFocus() : boolean {
            return this.appSelector.giveFocus();
        }
    }

    export class HomeMainContainerBuilder {

        backgroundImgUrl: string;

        appSelector: app.launcher.AppSelector;

        loginForm: app.login.LoginForm;

        appInfo: app.launcher.AppInfo;

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

        setAppInfo(value: app.launcher.AppInfo): HomeMainContainerBuilder {
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
