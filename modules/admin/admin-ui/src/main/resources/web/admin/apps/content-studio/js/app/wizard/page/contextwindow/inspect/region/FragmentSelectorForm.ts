module app.wizard.page.contextwindow.inspect.region {

    import ContentComboBox = api.content.ContentComboBox;

    export class FragmentSelectorForm extends api.ui.form.Form {

        private fragmentSelector: ContentComboBox;

        constructor(fragmentSelector: ContentComboBox, title?: string) {
            super('fragment-combobox-form');
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

        getSelector(): ContentComboBox {
            return this.fragmentSelector;
        }

    }
}