module app_wizard {

    export class SpaceForm extends api_ui.Panel {

        constructor() {
            super("SpaceForm");

            var form = new api_ui.Form();

            var templateFieldset = new api_ui.Fieldset("Template");

            var templateSelector = new api_ui.Dropdown("template");
            templateSelector.addOption("tpl1", "Template 1");
            templateSelector.addOption("tpl2", "Template 2");
            templateSelector.addOption("tpl3", "Template 3");

            templateFieldset.add(new api_ui.FormItem("Template", templateSelector));

            form.appendChild(templateFieldset);

            this.appendChild(form);
        }
    }
}
