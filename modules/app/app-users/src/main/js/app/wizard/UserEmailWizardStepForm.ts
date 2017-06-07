import '../../api.ts';

import Principal = api.security.Principal;

import EmailInput = api.ui.text.EmailInput;
import FormItemBuilder = api.ui.form.FormItemBuilder;
import Validators = api.ui.form.Validators;

import DivEl = api.dom.DivEl;
import LabelEl = api.dom.LabelEl;
import i18n = api.util.i18n;

export class UserEmailWizardStepForm extends api.app.wizard.WizardStepForm {

    private email: EmailInput;

    private userStoreKey: api.security.UserStoreKey;

    constructor(userStoreKey: api.security.UserStoreKey) {
        super();

        this.userStoreKey = userStoreKey;
        this.email = new EmailInput();
        this.email.setUserStoreKey(this.userStoreKey);

        let emailFormItem = new FormItemBuilder(this.email).setLabel(i18n('field.email')).setValidator(Validators.required).build();

        let fieldSet = new api.ui.form.Fieldset();
        fieldSet.add(emailFormItem);

        let form = new api.ui.form.Form(api.form.FormView.VALIDATION_CLASS).add(fieldSet);

        form.onFocus((event) => {
            this.notifyFocused(event);
        });
        form.onBlur((event) => {
            this.notifyBlurred(event);
        });

        form.onValidityChanged((event: api.ValidityChangedEvent) => {
            this.notifyValidityChanged(new api.app.wizard.WizardStepValidityChangedEvent(event.isValid()));
            emailFormItem.toggleClass('invalid', !event.isValid());
        });

        this.appendChild(form);
    }

    layout(principal: Principal) {
        this.email.setValue(principal.asUser().getEmail());
        this.email.setName(principal.asUser().getEmail());
        this.email.setOriginEmail(principal.asUser().getEmail());
    }

    isValid(): boolean {
        return this.email.isValid();
    }

    getEmail(): string {
        return this.email.getValue();
    }

    giveFocus(): boolean {
        return this.email.giveFocus();
    }
}
