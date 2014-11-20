module app.wizard {

    import Principal = api.security.Principal;
    import PrincipalKey = api.security.PrincipalKey;
    import AccessControlList = api.ui.security.acl.AccessControlList;
    import AccessControlEntry = api.security.acl.AccessControlEntry;

    export class SecurityWizardStepForm extends api.app.wizard.WizardStepForm {

        private comboBox: api.ui.security.acl.AccessControlEntryComboBox;
        private inheritCheckbox: api.ui.Checkbox;

        private inheritedPermissions: AccessControlList;

        constructor() {
            super();

            var label = new api.dom.LabelEl("Access Control Entry list");
            this.appendChild(label);
            this.comboBox = new api.ui.security.acl.AccessControlEntryComboBox();

            this.inheritCheckbox = new api.ui.Checkbox('Inherit permissions');
            this.inheritedPermissions = new AccessControlList('inherited');
            this.inheritedPermissions.setDoOffset(false);
            this.inheritCheckbox.onValueChanged((event: api.ui.ValueChangedEvent) => {
                this.inheritedPermissions.setVisible(event.getNewValue() == 'true');
            });

            this.appendChild(this.comboBox);
            this.appendChild(this.inheritCheckbox);
            this.appendChild(this.inheritedPermissions);

            // TEST
            var label2 = new api.dom.LabelEl("Principals list");
            this.appendChild(label2);
            var principals = new api.ui.security.PrincipalComboBox();
            this.appendChild(principals);
        }

        layout(content: api.content.Content) {
            content.getPermissions().getEntries().forEach((entry: AccessControlEntry) => {
                this.comboBox.select(entry)
            });
            var inheritedEntries = content.getInheritedPermissions().getEntries();
            this.inheritCheckbox.setChecked(inheritedEntries.length > 0);
            this.inheritedPermissions.setItems(inheritedEntries);
        }

        giveFocus(): boolean {
            return this.comboBox.giveFocus();
        }
    }
}
