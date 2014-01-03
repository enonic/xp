module app.wizard {

    export class MixinForm extends api.ui.form.Form {

        constructor() {
            super("MixinForm");

            var fieldset = new api.ui.form.Fieldset(this,"Config");
            this.fieldset(fieldset);

            var xmlTextArea:api.ui.CodeArea = new api.ui.CodeAreaBuilder().
                setName("xml").
                setMode("xml").
                setLineNumbers(true).
                setSize(api.ui.TextAreaSize.LARGE).
                build();

            fieldset.add(new api.ui.form.FormItem("XML", xmlTextArea));

        }
    }

}