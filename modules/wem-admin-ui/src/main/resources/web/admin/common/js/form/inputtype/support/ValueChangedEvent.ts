module api.form.inputtype.support {

    export class ValueChangedEvent {

        private newValue:api.data.Value;

        constructor(newValue:api.data.Value) {
            this.newValue = newValue;
        }

        getNewValue():api.data.Value {
            return this.newValue;
        }
    }
}