module api.app.wizard {

    export class WizardStepForm extends api.ui.form.Form {

        constructor(generateId?:boolean, className?:string) {
            super(generateId, className);
        }
    }
}