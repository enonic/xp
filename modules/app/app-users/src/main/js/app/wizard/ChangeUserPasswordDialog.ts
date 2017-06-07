import '../../api.ts';
import {OpenChangePasswordDialogEvent} from './OpenChangePasswordDialogEvent';

import Principal = api.security.Principal;
import PasswordGenerator = api.ui.text.PasswordGenerator;
import DialogButton = api.ui.dialog.DialogButton;
import FormItemBuilder = api.ui.form.FormItemBuilder;
import Validators = api.ui.form.Validators;
import DefaultErrorHandler = api.DefaultErrorHandler;
import i18n = api.util.i18n;

export class ChangeUserPasswordDialog extends api.ui.dialog.ModalDialog {

    private password: PasswordGenerator;

    private principal: Principal;

    private userPath: api.dom.H6El;

    private changePasswordButton: DialogButton;

    constructor() {
        super(i18n('dialog.changePassword.title'));

        this.getEl().addClass('change-password-dialog');

        this.userPath = new api.dom.H6El().addClass('user-path');
        let descMessage = new api.dom.H6El().addClass('desc-message').setHtml(i18n('dialog.changePassword.msg'));

        this.appendChildToContentPanel(this.userPath);
        this.appendChildToContentPanel(descMessage);

        this.password = new PasswordGenerator();
        this.password.onInput(() => this.toggleChangePasswordButton());
        this.password.onValidityChanged(() => this.toggleChangePasswordButton());

        this.onShown(() => this.toggleChangePasswordButton());

        let passwordFormItem = new FormItemBuilder(this.password).setLabel(i18n('field.password')).setValidator(Validators.required).build();

        let fieldSet = new api.ui.form.Fieldset();
        fieldSet.add(passwordFormItem);

        let form = new api.ui.form.Form().add(fieldSet);

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

        this.changePasswordButton = this.addAction(new api.ui.Action(i18n('action.changePassword'), '').onExecuted(() => {
            new api.security.SetUserPasswordRequest().setKey(this.principal.getKey()).setPassword(
                this.password.getValue()).sendAndParse().then((result) => {
                api.notify.showFeedback(i18n('notify.change.password'));
                this.close();
            }).catch(DefaultErrorHandler.handle);
        }));
        this.changePasswordButton.setEnabled(false);
    }

    private toggleChangePasswordButton() {
        if (this.password.getValue().length === 0) {
            this.changePasswordButton.setEnabled(false);
        } else {
            this.changePasswordButton.setEnabled(true);
        }
    }

    open() {
        super.open();
    }

    show() {
        api.dom.Body.get().appendChild(this);
        super.show();
    }

    close() {
        this.password.setValue('');
        super.close();
        this.remove();
    }

    getPrincipal(): Principal {
        return this.principal;
    }

}
