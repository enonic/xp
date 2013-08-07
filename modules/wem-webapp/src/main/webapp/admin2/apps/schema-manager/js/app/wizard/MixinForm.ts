module app_wizard {

    export class MixinForm extends api_ui.Panel {

        private xmlTextArea:api_ui.CodeArea;

        constructor() {
            super("MixinForm");

            var form = new api_ui.Form();

            var fieldset = new api_ui.Fieldset("Config");

            this.xmlTextArea = new api_ui.CodeArea("xml");
            this.xmlTextArea.setSize(api_ui.TextAreaSize.LARGE);
            this.xmlTextArea.setLineNumbers(true);


            fieldset.add(new api_ui.FormItem("XML", this.xmlTextArea));

            form.appendChild(fieldset);

            this.appendChild(form);
        }

        getXml():string {
            return this.xmlTextArea.getValue();
        }
    }

}