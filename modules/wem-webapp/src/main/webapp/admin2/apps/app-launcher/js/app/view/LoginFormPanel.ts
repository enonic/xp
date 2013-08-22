///<reference path='../model/UserStore.ts' />
///<reference path='../model/Authenticator.ts' />

module app_view {

    export class LoginFormPanel extends api_dom.DivEl {

        private licensedTo:api_dom.DivEl;
        private userStoresDropdown:api_ui.Dropdown;
        private userIdInput:api_ui.TextInput;
        private passwordInput:api_ui.PasswordInput;

        private authenticator:app_model.Authenticator;
        private userStores:{[userStoreId: string]: app_model.UserStore;};
        private onUserAuthenticatedHandler:(userName:string, userStore:app_model.UserStore) => void;

        constructor(authenticator:app_model.Authenticator) {
            super(null, 'admin-home-login-form');
            this.authenticator = authenticator;
            this.userStores = {};
            this.onUserAuthenticatedHandler = null;

            var formContainer = new api_dom.DivEl(null, 'admin-home-login-form-container');
            var title = new api_dom.H3El();
            title.setText('Login');
            this.userStoresDropdown = new api_ui.Dropdown('userstore');
            this.userStoresDropdown.addClass('admin-home-form-item');
            this.userIdInput = new api_ui.TextInput(null, 'admin-home-form-item');
            this.userIdInput.setPlaceholder('userid or e-mail');
            this.passwordInput = new api_ui.PasswordInput(null, 'admin-home-form-item');
            this.passwordInput.setPlaceholder('password');

            var loginButton = new api_ui.Button('Log in');
            loginButton.addClass('admin-home-login-button');
            loginButton.setClickListener((event) => {
                this.loginButtonClick();
            });

            formContainer.appendChild(title);
            formContainer.appendChild(this.userStoresDropdown);
            formContainer.appendChild(this.userIdInput);
            formContainer.appendChild(this.passwordInput);
            formContainer.appendChild(loginButton);
            this.appendChild(formContainer);

            this.licensedTo = new api_dom.DivEl(null, 'admin-home-login-licensed-to');
            this.appendChild(this.licensedTo);
        }

        setLicensedTo(value:string) {
            this.licensedTo.getEl().setInnerHtml(value);
        }

        setUserStores(userStores:app_model.UserStore[], defaultUserStore?:app_model.UserStore) {
            userStores.forEach((userStore:app_model.UserStore) => {
                this.userStoresDropdown.addOption(userStore.getId(), userStore.getName());
                this.userStores[userStore.getId()] = userStore;
            });
            if (defaultUserStore) {
                this.userStoresDropdown.setValue(defaultUserStore.getId());
            }
        }

        onUserAuthenticated(handler:(userName:string, userStore:app_model.UserStore) => void) {
            this.onUserAuthenticatedHandler = handler;
        }

        private loginButtonClick() {
            var userName = this.userIdInput.getValue();
            var password = this.passwordInput.getValue();
            var selectedUserStoreId = this.userStoresDropdown.getValue();
            if (userName === '' || password === '' || selectedUserStoreId === '') {
                return;
            }
            var userStore = this.userStores[selectedUserStoreId];
            if (this.authenticator.authenticate(userName, userStore, password)) {
                if (this.onUserAuthenticatedHandler) {
                    this.onUserAuthenticatedHandler(userName, userStore);
                }
            }
        }
    }

}
