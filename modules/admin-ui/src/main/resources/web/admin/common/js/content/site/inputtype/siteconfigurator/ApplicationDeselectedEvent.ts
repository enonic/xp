module api.content.site.inputtype.siteconfigurator {

    import FormView = api.form.FormView;
    import Application = api.application.Application;

    export class ApplicationDeselectedEvent {

        private deselectedApplication: Application;

        private formView: FormView;

        constructor(selectedApplication: Application, formView: FormView) {
            this.deselectedApplication = selectedApplication;
            this.formView = formView;
        }

        getDeselectedApplication(): Application {
            return this.deselectedApplication;
        }

        getFormView(): FormView {
            return this.formView;
        }
    }
}