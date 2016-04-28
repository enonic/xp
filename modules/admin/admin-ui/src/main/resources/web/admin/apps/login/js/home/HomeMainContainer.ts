import "../api.ts";
import {LoginForm} from "../auth/LoginForm";
import {Branding} from "./Branding";
import {CenterPanel} from "./CenterPanel";

export class HomeMainContainer extends api.dom.DivEl {

    private brandingPanel: Branding;

    private loginForm: LoginForm;

    private centerPanel: CenterPanel;

    constructor(builder: LoginHomeMainContainerBuilder) {
        super('home-main-container');

        var lazyImage = new api.ui.image.LazyImage(api.util.UriHelper.getAdminUri("/common/images/image1x1.png"));
        this.appendChild(lazyImage);

        this.getEl().setAttribute("tabindex", "100"); //Need tabindex to be able to focus element

        this.loginForm = builder.loginForm;

        this.brandingPanel = new Branding();

        this.centerPanel = new CenterPanel();
        this.centerPanel.prependChild(this.brandingPanel);
        this.centerPanel.addToLoginPanel(this.loginForm);

        this.appendChild(this.centerPanel);

        this.onAdded(() => {
            lazyImage.setSrc(api.util.UriHelper.getAdminUri("common/images/background-1920.jpg"));
        });
    }

    showLogin() {
        this.centerPanel.showLoginPanel();
        this.brandingPanel.show();
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

export class LoginHomeMainContainerBuilder {

    loginForm: LoginForm;

    setLoginForm(value: LoginForm): LoginHomeMainContainerBuilder {
        this.loginForm = value;
        return this;
    }

    build(): HomeMainContainer {
        return new HomeMainContainer(this);
    }
}
