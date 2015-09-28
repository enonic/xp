module app.wizard.page.contextwindow.inspect.page {

    export class PageControllerForm extends api.ui.form.Form {

        private controllerSelector: PageControllerSelector;

        constructor(controllerSelector: PageControllerSelector) {
            super('page-controller-form');
            this.controllerSelector = controllerSelector;

            var fieldSet = new api.ui.form.Fieldset();
            fieldSet.add(new api.ui.form.FormItemBuilder(controllerSelector).setLabel("Page Controller").build());
            this.add(fieldSet);
        }

        getSelector(): PageControllerSelector {
            return this.controllerSelector;
        }

    }
}