module api.content.site.inputtype.moduleconfigurator {

    import FormView = api.form.FormView;
    import Module = api.module.Module;

    export class ModuleDeselectedEvent {

        private deselectedModule: Module;

        private formView: FormView;

        constructor(selectedModule: Module, formView: FormView) {
            this.deselectedModule = selectedModule;
            this.formView = formView;
        }

        getDeselectedModule(): Module {
            return this.deselectedModule;
        }

        getFormView(): FormView {
            return this.formView;
        }
    }
}