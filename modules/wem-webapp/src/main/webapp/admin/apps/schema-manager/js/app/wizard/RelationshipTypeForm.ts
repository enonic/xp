module app.wizard {

    export class RelationshipTypeForm extends api.ui.form.Form {

        constructor() {
            super();

            var fieldset = new api.ui.form.Fieldset("Config");
            this.add(fieldset);

            var xmlTextArea: api.ui.CodeArea = new api.ui.CodeAreaBuilder().
                setName("xml").
                setMode("xml").
                setLineNumbers(true).
                setSize(api.ui.TextAreaSize.LARGE).
                build();

            fieldset.add(new api.ui.form.FormItem(new api.ui.form.FormItemBuilder(xmlTextArea).setLabel("XML")));
        }

    }


}