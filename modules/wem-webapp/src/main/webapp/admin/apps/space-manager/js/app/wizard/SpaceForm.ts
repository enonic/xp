module app_wizard {

    export class SpaceForm extends api_ui_form.Form {

        constructor() {
            super("SpaceForm");

            var templateFieldset = new api_ui_form.Fieldset(this, "Template");
            this.fieldset(templateFieldset);

            var templateSelector = new api_ui.Dropdown("template");
            templateSelector.addOption("tpl1", "Template 1");
            templateSelector.addOption("tpl2", "Template 2");
            templateSelector.addOption("tpl3", "Template 3");

            templateFieldset.add(new api_ui_form.FormItem("Template", templateSelector));



        }
    }
}
