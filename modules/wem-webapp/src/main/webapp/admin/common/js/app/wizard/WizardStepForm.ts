module api.app.wizard {

    export class WizardStepForm extends api.ui.form.Form {

        constructor(className?:string) {
            super(className);
            this.addClass("wizard-step-form");
        }
    }
}