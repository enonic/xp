module app.wizard {

    import AccessControlComboBox = api.security.acl.AccessControlComboBox;
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

            var inherited = content.getInheritedPermissions().getEntries();
            //TODO: temp fix until inherited flag comes from backend
            inherited.forEach((ace) => {
                ace.setInherited(true);
            });

            var permissions = [].concat(content.getPermissions().getEntries(), inherited);
            var keyFunction = (ace: AccessControlEntry) => {
                return ace.getPrincipalKey().toString();
            };
            this.originalValues = api.util.ArrayHelper.removeDuplicates(permissions, keyFunction).sort();

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
    }
}
