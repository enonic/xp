module app.wizard {

    import Principal = api.security.Principal;
    import PasswordGenerator = api.ui.text.PasswordGenerator;
    import DialogButton = api.ui.dialog.DialogButton;
    import FormItemBuilder = api.ui.form.FormItemBuilder;
    import Validators = api.ui.form.Validators;

    export class ChangeUserPasswordDialog extends api.ui.dialog.ModalDialog {

        private password: PasswordGenerator;

        private principal: Principal;

        private userPath: api.dom.H6El;

        private changePasswordButton: DialogButton;

        constructor() {
            super({
                title: new api.ui.dialog.ModalDialogHeader("Change password")
            });

            this.getEl().addClass("change-password-dialog");

            this.userPath = new api.dom.H6El().addClass("user-path");
            var descMessage = new api.dom.H6El().addClass("desc-message").
                setHtml("Password will be updated immediately after finishing");

            this.appendChildToContentPanel(this.userPath);
            this.appendChildToContentPanel(descMessage);

            this.password = new PasswordGenerator();
            this.password.onInput(() => {
               if(this.password.getValue().length == 0) {
                    this.changePasswordButton.setEnabled(false);
               } else {
                   this.changePasswordButton.setEnabled(true);
               }
            });

            var passwordFormItem = new FormItemBuilder(this.password).
                setLabel('Password').
                setValidator(Validators.required).
                build();

            var fieldSet = new api.ui.form.Fieldset();
            fieldSet.add(passwordFormItem);

            var form = new api.ui.form.Form().add(fieldSet);

            this.appendChildToContentPanel(form);
            this.initializeActions();

            OpenChangePasswordDialogEvent.on((event) => {
                this.principal = event.getPrincipal();
                this.userPath.setHtml(this.principal.getKey().toPath());
                this.open();
            });

            this.addCancelButtonToBottom();
        }

        private initializeActions() {

            this.changePasswordButton = this.addAction(new api.ui.Action("Change Password", "").onExecuted(() => {
                new api.security.SetUserPasswordRequest().
                    setKey(this.principal.getKey()).
                    setPassword(this.password.getValue()).sendAndParse().then((result) => {
                        api.notify.showFeedback('Password was changed!');
                        this.close();
                    });
            }));
            this.changePasswordButton.setEnabled(false);
        }

        open() {
            super.open();
        }

        show() {
            api.dom.Body.get().appendChild(this);
            super.show();
        }

        close() {
            this.password.setValue("");
            super.close();
            this.remove();
        }

        getPrincipal(): Principal {
            return this.principal;
        }

    }


}