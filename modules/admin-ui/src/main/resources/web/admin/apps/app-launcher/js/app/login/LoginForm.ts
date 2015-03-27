module app.login {

    import UserStore = api.security.UserStore;

    export class LoginForm extends api.dom.FormEl {

        private messageContainer: api.dom.DivEl;
        private userIdInput: api.ui.text.TextInput;
        private passwordInput: api.ui.text.PasswordInput;
        private loginButton: api.ui.button.Button;

        private authenticator: Authenticator;
        private userStores: {[userStoreId: string]: UserStore;};
        private onUserAuthenticatedHandler: (loginResult: api.security.auth.LoginResult) => void;

        constructor(authenticator: Authenticator) {
            super('login-form');
            this.authenticator = authenticator;
            this.userStores = {};
            this.onUserAuthenticatedHandler = null;

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

            this.loginButton = new api.ui.button.Button();
            this.loginButton.addClass("login-button");
            this.loginButton.hide();
            this.loginButton.onClicked(() => {
                this.loginButtonClick();
            })


            this.messageContainer = new api.dom.DivEl("message-container");

            var passwordDiv = new api.dom.DivEl("password-container");
            passwordDiv.appendChild(this.passwordInput);
            passwordDiv.appendChild(this.loginButton);

            this.appendChild(this.userIdInput);
            this.appendChild(passwordDiv);
            this.appendChild(this.messageContainer);

            this.onShown((event) => {
                this.userIdInput.getEl().simulate("click");// for mobile devices
                this.userIdInput.giveFocus();
                var checkLoginButtonInterval = setInterval(() => {  //workaround to show login button when browser autofills inputs
                    if(this.checkFieldsNotEmpty()) {
                        this.loginButton.show();
                        clearInterval(checkLoginButtonInterval);
                    } else {
                        this.loginButton.hide();
                    }
                }, 100);
            });
            this.onHidden((event) => {
                (<HTMLElement>document.activeElement).blur();
            });
        }

        onUserAuthenticated(handler: (loginResult: api.security.auth.LoginResult) => void) {
            this.onUserAuthenticatedHandler = handler;
        }

        hide() {
            super.hide();
        }

        private loginButtonClick() {
            var userName = this.userIdInput.getValue();
            var password = this.passwordInput.getValue();
            if (userName === '' || password === '') {
                return;
            }

            this.userIdInput.removeClass('invalid');
            this.passwordInput.removeClass('invalid');
            this.loginButton.removeClass('invalid');

            this.authenticator.authenticate(userName, password,
                (loginResult: api.security.auth.LoginResult) => this.handleAuthenticateResponse(loginResult));
        }

        private handleAuthenticateResponse(loginResult: api.security.auth.LoginResult) {
            if (loginResult.isAuthenticated()) {
                if (this.onUserAuthenticatedHandler) {
                    this.onUserAuthenticatedHandler(loginResult);
                }
                this.passwordInput.setValue('');
                this.setMessage('');
            } else {
                this.setMessage(loginResult.getMessage() || 'Login failed!');
                this.passwordInput.giveFocus();
                this.userIdInput.addClass('invalid');
                this.passwordInput.addClass('invalid');
                this.loginButton.addClass('invalid');
            }
        }

        public setMessage(text: string) {
            this.messageContainer.setHtml(text);
        }

        private onInputTyped(event: KeyboardEvent) {
            this.userIdInput.removeClass('invalid');
            this.passwordInput.removeClass('invalid');
            this.loginButton.removeClass('invalid');


            var fieldsNotEmpty: boolean = this.checkFieldsNotEmpty();

            if(fieldsNotEmpty) {
                this.loginButton.show();
                if (event.keyCode == 13) {
                    this.loginButtonClick();
                } else {
                    this.setMessage('');
                }
            } else {
                this.loginButton.hide();
                this.setMessage('');
            }
        }

        private checkFieldsNotEmpty(): boolean {
            var fieldsNotEmpty: boolean = (this.userIdInput.getValue() !== '') && (this.passwordInput.getValue() !== '');
            return fieldsNotEmpty;
        }
    }

}
