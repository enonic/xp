module app.login {

    export class LoginForm extends api.dom.DivEl {

        private licensedTo: api.dom.DivEl;
        private userStoresDropdown: api.ui.Dropdown;
        private userIdInput: api.ui.TextInput;
        private passwordInput: api.ui.PasswordInput;
        private loginButton: api.ui.Button;

        private authenticator: Authenticator;
        private userStores: {[userStoreId: string]: UserStore;};
        private onUserAuthenticatedHandler: (userName: string, userStore: UserStore) => void;

        constructor(authenticator: Authenticator) {
            super('login-form');
            this.authenticator = authenticator;
            this.userStores = {};
            this.onUserAuthenticatedHandler = null;

            var formContainer = new api.dom.DivEl();
            var title = new api.dom.H3El();
            title.setText(_i18n('Login'));
            this.userStoresDropdown = new api.ui.Dropdown('userstore');
            this.userStoresDropdown.addClass('form-item');
            this.userIdInput = new api.ui.TextInput('form-item');
            this.userIdInput.setPlaceholder(_i18n('userid or e-mail'));
            this.passwordInput = new api.ui.PasswordInput('form-item');
            this.passwordInput.setPlaceholder(_i18n('password'));
            this.userIdInput.onKeyUp((event: KeyboardEvent) => {
                this.onInputTyped(event);
            });
            this.passwordInput.onKeyUp((event: KeyboardEvent) => {
                this.onInputTyped(event);
            });

            this.loginButton = new api.ui.Button(_i18n('Log in'));
            this.loginButton.addClass('login-button').addClass('disabled');
            this.loginButton.onClicked((event: MouseEvent) => {
                this.loginButtonClick();
            });

            formContainer.appendChild(title);
            formContainer.appendChild(this.userStoresDropdown);
            formContainer.appendChild(this.userIdInput);
            formContainer.appendChild(this.passwordInput);
            formContainer.appendChild(this.loginButton);
            this.appendChild(formContainer);

            this.licensedTo = new api.dom.DivEl('login-licensed-to');
            this.appendChild(this.licensedTo);
        }

        setLicensedTo(value: string) {
            this.licensedTo.getEl().setInnerHtml(value);
        }

        setUserStores(userStores: UserStore[], defaultUserStore?: UserStore) {
            userStores.forEach((userStore: UserStore) => {
                this.userStoresDropdown.addOption(userStore.getId(), userStore.getName());
                this.userStores[userStore.getId()] = userStore;
            });
            if (defaultUserStore) {
                this.userStoresDropdown.setValue(defaultUserStore.getId());
            }
        }

        onUserAuthenticated(handler: (userName: string, userStore: UserStore) => void) {
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

        private onInputTyped(event: KeyboardEvent) {
            var fieldsNotEmpty: boolean = (this.userIdInput.getValue() !== '') && (this.passwordInput.getValue() !== '');
            if (fieldsNotEmpty) {
                this.loginButton.removeClass('disabled');
            } else {
                this.loginButton.addClass('disabled');
            }
            this.loginButton.setEnabled(fieldsNotEmpty);
            if (fieldsNotEmpty && event.keyCode == 13) {
                this.loginButtonClick();
            }
        }
    }

}
