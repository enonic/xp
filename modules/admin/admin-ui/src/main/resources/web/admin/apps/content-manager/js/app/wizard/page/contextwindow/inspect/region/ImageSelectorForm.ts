module app.wizard.page.contextwindow.inspect.region {

    import ContentComboBox = api.content.ContentComboBox;

    export class ImageSelectorForm extends api.ui.form.Form {

        private imageSelector: ContentComboBox;

        constructor(templateSelector: ContentComboBox, title?: string) {
            super('image-combobox-form');
            this.imageSelector = templateSelector;

            var fieldSet = new api.ui.form.Fieldset();
            if (!api.util.StringHelper.isBlank(title)) {
                fieldSet.add(new api.ui.form.FormItemBuilder(templateSelector).setLabel(title).build());
            }
            else {
                fieldSet.add(new api.ui.form.FormItemBuilder(templateSelector).build());
            }

            this.add(fieldSet);
        }

        getSelector(): ContentComboBox {
            return this.imageSelector;
        }

    }
}