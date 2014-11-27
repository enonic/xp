module app.wizard {

    import Principal = api.security.Principal;
    import PrincipalKey = api.security.PrincipalKey;
    import AccessControlListView = api.security.acl.AccessControlListView;
    import AccessControlComboBox = api.security.acl.AccessControlComboBox;
    import AccessControlEntry = api.security.acl.AccessControlEntry;

    export class SecurityWizardStepForm extends api.app.wizard.WizardStepForm {

        private content: api.content.Content;

        private comboBox: AccessControlComboBox;
        private inheritCheckbox: api.ui.Checkbox;

        private inheritedPermissions: AccessControlListView;

        constructor() {
            super("security-wizard-step-form");

            var form = new api.ui.form.Form();
            var fieldSet = new api.ui.form.Fieldset();
            form.add(fieldSet);

            this.comboBox = new AccessControlComboBox();
            var selectionChangeListener = () => {
                this.inheritCheckbox.toggleClass('separator', this.comboBox.getSelectedValues().length > 0);
            };
            this.comboBox.onOptionDeselected(selectionChangeListener);
            this.comboBox.onOptionSelected(selectionChangeListener);

            this.inheritedPermissions = new AccessControlListView('inherited');
            this.inheritedPermissions.setItemsEditable(false);

            this.inheritCheckbox = new api.ui.Checkbox('Inherit permissions');
            this.inheritCheckbox.onValueChanged((event: api.ui.ValueChangedEvent) => {
                var checked = event.getNewValue() == 'true';
                this.inheritedPermissions.setVisible(checked);
                if (this.content) {
                    if (!checked) {
                        // turning off inherited, so restore custom permissions
                        var entries = this.content.getPermissions().getEntries();
                        if (entries.length > 0) {
                            this.selectPermissions(entries);
                        }
                    } else {
                        // turning on inherited, so filter out matches in custom permissions
                        var inheritedEntries = this.content.getInheritedPermissions().getEntries();
                        if (inheritedEntries.length > 0) {
                            this.deselectPermissions(inheritedEntries);
                        }
                    }
                }
            });

            // add elements to composite element to appear under one label
            this.comboBox.addAdditionalElement(this.inheritCheckbox);
            this.comboBox.addAdditionalElement(this.inheritedPermissions);

            fieldSet.add(new api.ui.form.FormItemBuilder(this.comboBox).setLabel("Permissions").build());
            this.appendChild(form);
        }

        layout(content: api.content.Content) {
            this.content = content;
            this.selectPermissions(content.getPermissions().getEntries());

            var inheritedEntries = content.getInheritedPermissions().getEntries();
            this.inheritedPermissions.setItems(inheritedEntries);

            this.inheritCheckbox.setChecked(inheritedEntries.length > 0);
        }

        private selectPermissions(toSelect: AccessControlEntry[]) {
            toSelect.forEach((item) => {
                if (!this.comboBox.isSelected(item)) {
                    this.comboBox.select(item);
                }
            });
        }

        private deselectPermissions(toDeselect: AccessControlEntry[]) {
            this.comboBox.getSelectedValues().forEach((selectedPermission: AccessControlEntry) => {
                for (var i = 0; i < toDeselect.length; i++) {
                    if (toDeselect[i].getPrincipalKey().equals(selectedPermission.getPrincipalKey())) {
                        this.comboBox.deselect(selectedPermission);
                        break;
                    }
                }
            });
        }

        giveFocus(): boolean {
            return this.comboBox.giveFocus();
        }
    }
}
