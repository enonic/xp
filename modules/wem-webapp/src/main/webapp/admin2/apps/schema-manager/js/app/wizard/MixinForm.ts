module app_wizard {

    export class MixinForm extends api_ui.Panel {

        constructor() {
            super("MixinForm");

            var form = new api_ui.Form();

            var fieldset = new api_ui.Fieldset("Config");

            var textArea = new api_ui.CodeArea("xml");
            textArea.setSize(api_ui.TextAreaSize.LARGE);
            textArea.setLineNumbers(true);


            fieldset.add(new api_ui.FormItem("XML", textArea));

            form.appendChild(fieldset);

            this.appendChild(form);
        }
    }
}