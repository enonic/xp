module api.app.wizard {

    export class WizardStepForm extends api.ui.Panel {

        constructor(className?:string) {
            super(className);
            this.addClass("wizard-step-form");
        }
    }
}