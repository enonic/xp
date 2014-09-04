module api.form.inputtype.support {

    export class ValueChangedEvent {

        private newValue: api.data.Value;

        constructor(newValue: api.data.Value) {
            api.util.assertNotNull(newValue, "sending ValueChangedEvent-s for null values is not allowed");
            this.newValue = newValue;
        }

        getNewValue(): api.data.Value {
            return this.newValue;
        }
    }
}