module app.home {

    export class CenterPanel extends api.dom.DivEl {

        private loginPanel:api.dom.DivEl;
        private appSelectorPanel:api.dom.DivEl;

        constructor() {
            super('center-panel');

            this.loginPanel = new api.dom.DivEl('login-panel');
            this.appSelectorPanel = new api.dom.DivEl('app-selector-panel');
            this.appendChild(this.loginPanel);
            this.appendChild(this.appSelectorPanel);
        }

        addToLoginPanel(el: api.dom.Element) {
            this.loginPanel.appendChild(el);
        }

        addToAppSelectorPanel(el: api.dom.Element) {
            this.appSelectorPanel.appendChild(el);
        }

        showLoginPanel() {
            this.appSelectorPanel.hide();
            this.loginPanel.show();
        }

        showAppSelectorPanel() {
            this.loginPanel.hide();
            this.appSelectorPanel.show();
        }
    }

}
