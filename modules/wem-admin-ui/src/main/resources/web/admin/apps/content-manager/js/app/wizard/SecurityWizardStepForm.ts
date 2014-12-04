module app.wizard {

    import AccessControlComboBox = api.ui.security.acl.AccessControlComboBox;
    import AccessControlEntry = api.security.acl.AccessControlEntry;

    export class SecurityWizardStepForm extends api.app.wizard.WizardStepForm {

        private content: api.content.Content;
        private comboBox: AccessControlComboBox;
        // save them to keep track of the modified state
        private originalValues: AccessControlEntry[];

        constructor() {
            super("security-wizard-step-form");

            var form = new api.ui.form.Form();
            this.appendChild(form);

            var fieldSet = new api.ui.form.Fieldset();
            form.add(fieldSet);

            this.comboBox = new AccessControlComboBox();
            var restoreLink = new api.dom.AEl('reset-link disabled');
            restoreLink.setHtml('Restore');
            restoreLink.onClicked((event: MouseEvent) => {
                if (!restoreLink.hasClass('disabled')) {
                    this.layout(this.content);
                }
            });
            this.comboBox.addAdditionalElement(restoreLink);

            var changeListener = () => {
                var selectedValues = this.comboBox.getSelectedDisplayValues().sort();
                restoreLink.toggleClass('disabled', api.ObjectHelper.arrayEquals(this.originalValues, selectedValues));
            };
            this.comboBox.onOptionValueChanged(changeListener);
            this.comboBox.onOptionSelected(changeListener);
            this.comboBox.onOptionDeselected(changeListener);

            fieldSet.add(new api.ui.form.FormItemBuilder(this.comboBox).setLabel("Permissions").build());
        }

        layout(content: api.content.Content) {
            this.comboBox.clearSelection();

            var inheritedPermissions = content.getInheritedPermissions();
            var inheritedPermissionsEntries: AccessControlEntry[] = inheritedPermissions.getEntries();
            var contentPermissions = content.getPermissions();
            var contentPermissionsEntries: AccessControlEntry[] = contentPermissions.getEntries();

            // merge inherited and content permissions, if overwritten in content skip inherited entry
            var permissions: AccessControlEntry[] = [];
            inheritedPermissionsEntries.forEach((ace) => {
                ace.setInherited(true);
                var principalKey = ace.getPrincipalKey();

                if (contentPermissions.contains(principalKey)) {
                    permissions.push(contentPermissions.getEntry(principalKey).clone());
                } else {
                    permissions.push(ace.clone());
                }
            });

            contentPermissionsEntries.forEach((ace) => {
                if (!inheritedPermissions.contains(ace.getPrincipalKey())) {
                    permissions.push(ace.clone());
                }
            });

            console.log('ACL parent  ', inheritedPermissions.toString());
            console.log('ACL content ', contentPermissions.toString());
            console.log('ACL combined', new api.security.acl.AccessControlList(permissions).toString());
            this.originalValues = permissions.sort();

            this.originalValues.forEach((item) => {
                if (!this.comboBox.isSelected(item)) {
                    this.comboBox.select(item);
                }
            });

            this.content = content;
        }

        giveFocus(): boolean {
            return this.comboBox.giveFocus();
        }

        getEntries(): AccessControlEntry[] {
            return this.comboBox.getSelectedDisplayValues();
        }
    }
}
