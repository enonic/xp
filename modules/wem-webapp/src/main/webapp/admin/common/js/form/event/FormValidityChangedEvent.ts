module api.form.event {

    export class FormValidityChangedEvent extends FormEvent {

        private valid:boolean;

        private validationRecorder:api.form.ValidationRecorder;

        constructor(valid:boolean, validationRecorder?:api.form.ValidationRecorder) {
            super();
            this.valid = valid;
            this.validationRecorder = validationRecorder;
        }

        isValid( ):boolean {
            return this.valid;
        }

        getValidationRecorder():api.form.ValidationRecorder {
            return this.validationRecorder;
        }
    }
}