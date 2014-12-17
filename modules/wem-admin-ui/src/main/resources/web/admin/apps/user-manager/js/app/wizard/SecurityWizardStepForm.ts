module app.wizard {

    import UserStoreAccessControlList = api.security.acl.UserStoreAccessControlList;
    import UserStoreAccessControlListView = api.ui.security.acl.UserStoreAccessControlListView;
    import UserStoreAccessControlEntryView = api.ui.security.acl.UserStoreAccessControlEntryView;
    import UserStoreAccessControlEntry = api.security.acl.UserStoreAccessControlEntry;
    import UserStoreAccessControlComboBox = api.ui.security.acl.UserStoreAccessControlComboBox;
    import Content = api.content.Content;
    import UserStore = api.security.UserStore;

    import DivEl = api.dom.DivEl;
    import LabelEl = api.dom.LabelEl;
    import AEl = api.dom.AEl;

    export class SecurityWizardStepForm extends api.app.wizard.WizardStepForm {

        private label: LabelEl;
        private inheritance: DivEl;
        private comboBox: UserStoreAccessControlComboBox;
        private userStore: UserStore;

        private content: Content;

        constructor() {
            super("security-wizard-step-form");

            var label = new DivEl("input-label"),
                wrapper = new DivEl("wrapper required");
            this.label = new LabelEl("Permissions");
            wrapper.appendChild(this.label);
            label.appendChild(wrapper);

            this.inheritance = new DivEl(/*"inheritance"*/);

            this.comboBox = new UserStoreAccessControlComboBox();
            this.comboBox.addClass('principal-combobox');


            var formView = new DivEl("form-view"),
                inputView = new DivEl("input-view valid"),
                inputTypeView = new DivEl("input-type-view"),
                inputOccurrenceView = new DivEl("input-occurrence-view single-occurrence"),
                inputWrapper = new DivEl("input-wrapper");

            inputWrapper.appendChild(this.inheritance);
            inputWrapper.appendChild(this.comboBox);

            inputOccurrenceView.appendChild(inputWrapper);
            inputTypeView.appendChild(inputOccurrenceView);
            inputView.appendChild(label);
            inputView.appendChild(inputTypeView);
            formView.appendChild(inputView);

            this.appendChild(formView);

        }

        layout(userStore: UserStore) {
            this.userStore = userStore;

            this.comboBox.clearSelection();
            userStore.getPermissions().getEntries().forEach((item) => {
                if (!this.comboBox.isSelected(item)) {
                    this.comboBox.select(item);
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
}
