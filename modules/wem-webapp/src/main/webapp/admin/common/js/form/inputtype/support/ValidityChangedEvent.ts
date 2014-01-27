module api.form.inputtype.support {

    export class ValidityChangedEvent extends InputTypeEvent {

        private valid:boolean;

        constructor(isValid:boolean) {
            super();
            this.valid = isValid;
        }

        isValid():boolean {
            return this.valid;
        }
    }
}