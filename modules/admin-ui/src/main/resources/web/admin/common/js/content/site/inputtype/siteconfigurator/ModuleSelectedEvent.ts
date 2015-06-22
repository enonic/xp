module api.content.site.inputtype.siteconfigurator {

    import FormView = api.form.FormView;
    import Module = api.module.Module;

    export class ModuleSelectedEvent {

        private selectedModule: Module;

        private formView: FormView;

        constructor(selectedModule: Module, formView: FormView) {
            this.selectedModule = selectedModule;
            this.formView = formView;
        }

        getSelectedModule(): Module {
            return this.selectedModule;
        }

        getFormView(): FormView {
            return this.formView;
        }
    }
}