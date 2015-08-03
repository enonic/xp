module api.content.site.inputtype.siteconfigurator {

    import FormView = api.form.FormView;
    import Application = api.application.Application;

    export class ModuleSelectedEvent {

        private selectedApplication: Application;

        private formView: FormView;

        constructor(selectedApplication: Application, formView: FormView) {
            this.selectedApplication = selectedApplication;
            this.formView = formView;
        }

        getSelectedApplication(): Application {
            return this.selectedApplication;
        }

        getFormView(): FormView {
            return this.formView;
        }
    }
}