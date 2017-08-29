import '../../api.ts';

import UserStoreAccessControlList = api.security.acl.UserStoreAccessControlList;
import UserStoreAccessControlComboBox = api.ui.security.acl.UserStoreAccessControlComboBox;
import UserStore = api.security.UserStore;
import FormItemBuilder = api.ui.form.FormItemBuilder;
import Validators = api.ui.form.Validators;

import DivEl = api.dom.DivEl;
import LabelEl = api.dom.LabelEl;
import i18n = api.util.i18n;

export class SecurityWizardStepForm extends api.app.wizard.WizardStepForm {

    private inheritance: DivEl;
    private comboBox: UserStoreAccessControlComboBox;
    private userStore: UserStore;

    constructor() {
        super('security-wizard-step-form');

        this.inheritance = new DivEl(/*'inheritance'*/);

        this.comboBox = new UserStoreAccessControlComboBox();
        this.comboBox.addClass('principal-combobox');

        let accessComboBoxFormItem = new FormItemBuilder(this.comboBox)
            .setValidator(Validators.required)
            .setLabel(i18n('field.permissions')).build();

        let fieldSet = new api.ui.form.Fieldset();
        fieldSet.add(accessComboBoxFormItem);

        let form = new api.ui.form.Form().add(fieldSet);

        form.onFocus((event) => {
            this.notifyFocused(event);
        });
        form.onBlur((event) => {
            this.notifyBlurred(event);
        });

        this.appendChild(this.inheritance);
        this.appendChild(form);

    }

    layout(userStore: UserStore, defaultUserStore: UserStore) {
        this.userStore = userStore;

        this.comboBox.clearSelection();

        if (defaultUserStore) {
            defaultUserStore.getPermissions().getEntries().forEach((item) => {
                this.comboBox.select(item, true);
            });
        }

        userStore.getPermissions().getEntries().forEach((item) => {
            if (!this.comboBox.isSelected(item)) {
                this.comboBox.select(item);
            }
        });

    }

    layoutReadOnly(userStore: UserStore) {
        this.userStore = userStore;

        this.comboBox.clearSelection();
        userStore.getPermissions().getEntries().forEach((item) => {
            if (!this.comboBox.isSelected(item)) {
                this.comboBox.select(item, true);
            }
        });

    }

    giveFocus(): boolean {
        return this.comboBox.giveFocus();
    }

    getPermissions(): UserStoreAccessControlList {
        return new api.security.acl.UserStoreAccessControlList(this.comboBox.getSelectedDisplayValues());
    }

}
