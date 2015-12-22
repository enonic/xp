module app {

    export class Main {

        private homeMainContainer: app.home.LoginHomeMainContainer;
        private loginForm: app.login.LoginForm;

        start() {
            this.homeMainContainer = this.createHomeMainContainer();
            api.dom.Body.get().appendChild(this.homeMainContainer);
            this.homeMainContainer.showLogin();
        }

        private createHomeMainContainer(): app.home.LoginHomeMainContainer {
            var linksContainer = new app.home.LinksContainer().
                addLink('Community', 'https://enonic.com/community').
                addLink('Documentation', 'https://enonic.com/docs/latest/').
                addText('v' + CONFIG.xpVersion);

            this.loginForm = new app.login.LoginForm(new app.login.AuthenticatorImpl());
            this.loginForm.onUserAuthenticated(this.onUserAuthenticated.bind(this));

            return new app.home.LoginHomeMainContainerBuilder().
                setLinksContainer(linksContainer).
                setLoginForm(this.loginForm).
                build();
        }

        private onUserAuthenticated(loginResult: api.security.auth.LoginResult) {
            window.location.href = "..";
        }
    }

}