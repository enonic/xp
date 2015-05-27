module app.wizard.page.contextwindow.inspect.region {

    import PartDescriptorDropdown = api.content.page.region.PartDescriptorDropdown;
    import Dropdown = api.ui.selector.dropdown.Dropdown;

    export class DescriptorBasedDropdownForm extends api.ui.form.Form {

        private templateSelector: Dropdown<api.content.page.Descriptor>;

        constructor(templateSelector: Dropdown<api.content.page.Descriptor>) {
            super('descriptor-based-dropdown-form');
            this.templateSelector = templateSelector;

            var fieldSet = new api.ui.form.Fieldset();
            fieldSet.add(new api.ui.form.FormItemBuilder(templateSelector).build());
            this.add(fieldSet);
        }

        getSelector(): Dropdown<api.content.page.Descriptor> {
            return this.templateSelector;
        }

    }
}