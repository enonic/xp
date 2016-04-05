module app.wizard.page.contextwindow.inspect.region {

    import FragmentDropdown = api.content.page.region.FragmentDropdown;

    export class FragmentSelectorForm extends api.ui.form.Form {

        private fragmentSelector: FragmentDropdown;

        constructor(fragmentSelector: FragmentDropdown, title?: string) {
            super('fragment-dropdown-form');
            this.fragmentSelector = fragmentSelector;

            var fieldSet = new api.ui.form.Fieldset();
            if (!api.util.StringHelper.isBlank(title)) {
                fieldSet.add(new api.ui.form.FormItemBuilder(fragmentSelector).setLabel(title).build());
            }
            else {
                fieldSet.add(new api.ui.form.FormItemBuilder(fragmentSelector).build());
            }

            this.add(fieldSet);
        }

        getSelector(): FragmentDropdown {
            return this.fragmentSelector;
        }

    }
}