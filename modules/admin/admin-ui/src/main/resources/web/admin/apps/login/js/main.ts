import "./api.ts";
import "./i18n/Messages_no.ts";
import {AuthenticatorImpl} from "./auth/Authenticator";
import {LoginForm} from "./auth/LoginForm";
import {HomeMainContainer, LoginHomeMainContainerBuilder} from "./home/HomeMainContainer";

declare var CONFIG;

class Main {

    private homeMainContainer: HomeMainContainer;
    private loginForm: LoginForm;

    start() {
        this.homeMainContainer = this.createHomeMainContainer();
        api.dom.Body.get().appendChild(this.homeMainContainer);
        this.homeMainContainer.showLogin();
    }

    private createHomeMainContainer(): HomeMainContainer {
        this.loginForm = new LoginForm(new AuthenticatorImpl());
        this.loginForm.onUserAuthenticated(this.onUserAuthenticated.bind(this));

        return new LoginHomeMainContainerBuilder().setLoginForm(this.loginForm).build();
    }

    private onUserAuthenticated(loginResult: api.security.auth.LoginResult) {
        window.location.href = CONFIG.callback ? CONFIG.callback : "..";
    }
}

window.onload = function () {
    new Main().start();
};
