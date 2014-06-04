module app.home {

    export class HomeMainContainer extends api.dom.DivEl {

        private brandingPanel: Branding;

        private appSelector: app.launcher.AppSelector;

        private loginForm: app.login.LoginForm;

        private linksContainer: LinksContainer;

        private centerPanel: CenterPanel;

        private backgroundImgUrl: string;

        constructor(builder: HomeMainContainerBuilder) {
            super('home-main-container');

            this.appSelector = builder.appSelector;
            this.loginForm = builder.loginForm;
            this.linksContainer = builder.linksContainer;
            this.backgroundImgUrl = builder.backgroundImgUrl;

            this.setBackgroundImgUrl(this.backgroundImgUrl);

            this.brandingPanel = new Branding();

            this.centerPanel = new CenterPanel();
            this.centerPanel.appendChild(this.brandingPanel);
            this.centerPanel.appendChild(this.appSelector);
            this.centerPanel.appendChild(this.loginForm);
            this.centerPanel.appendChild(this.linksContainer);
            this.appendChild(this.centerPanel);

            this.onScrolled((event) => {
                if (event.deltaY > 0) {
                    this.appSelector.highlightNextAppTile();
                } else if (event.deltaY < 0) {
                    this.appSelector.highlightPreviousAppTile();
                }
            })

            this.appSelector.onAppSelected((event) => {
                this.setBackgroundImgUrl("");
            });
        }

        giveFocus(): boolean {
            return this.appSelector.giveFocus();
        }

        show() {
            this.appSelector.showAppsCount();
            api.ui.KeyBindings.get().bindKeys(this.appSelector.getKeyBindings());
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
