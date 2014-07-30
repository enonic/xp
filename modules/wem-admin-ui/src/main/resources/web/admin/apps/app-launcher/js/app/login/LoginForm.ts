module app.login {

    export class LoginForm extends api.dom.DivEl {

        private userStoresDropdown: api.ui.Dropdown;
        private userIdInput: api.ui.text.TextInput;
        private passwordInput: api.ui.text.PasswordInput;
        private loginButton: api.ui.button.Button;

        private authenticator: Authenticator;
        private userStores: {[userStoreId: string]: UserStore;};
        private onUserAuthenticatedHandler: (userName: string, userStore: UserStore) => void;

        constructor(authenticator: Authenticator) {
            super('login-form');
            this.authenticator = authenticator;
            this.userStores = {};
            this.onUserAuthenticatedHandler = null;

            var formContainer = new api.dom.DivEl();
            this.userStoresDropdown = new api.ui.Dropdown('userstore');
            this.userStoresDropdown.addClass('form-item');
            this.userIdInput = new api.ui.text.TextInput('form-item');
            this.userIdInput.setPlaceholder(_i18n('userid or e-mail'));
            this.passwordInput = new api.ui.text.PasswordInput('form-item');
            this.passwordInput.setPlaceholder(_i18n('password'));
            this.userIdInput.onKeyUp((event: KeyboardEvent) => {
                this.onInputTyped(event);
            });
            this.passwordInput.onKeyUp((event: KeyboardEvent) => {
                this.onInputTyped(event);
            });

            this.loginButton = new api.ui.button.Button(_i18n('Sign in'));
            this.loginButton.addClass('login-button').addClass('disabled');
            this.loginButton.onClicked((event: MouseEvent) => {
                this.loginButtonClick();
            });

            var selectContainer = new api.dom.DivEl("select-container");
            selectContainer.appendChild(this.userStoresDropdown);

            formContainer.appendChild(selectContainer);
            formContainer.appendChild(this.userIdInput);
            formContainer.appendChild(this.passwordInput);
            formContainer.appendChild(this.loginButton);
            this.appendChild(formContainer);

            this.onShown((event) => {
                this.userIdInput.giveFocus();
            })
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

        hide() {
            super.hide();
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
