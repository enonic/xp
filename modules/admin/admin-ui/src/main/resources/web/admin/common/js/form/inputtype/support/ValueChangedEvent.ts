module api.form.inputtype.support {

    import Value = api.data.Value;

    export class ValueChangedEvent {

        private newValue: Value;

        constructor(newValue: Value) {
            api.util.assertNotNull(newValue, "sending ValueChangedEvent-s for null values is not allowed");
            this.newValue = newValue;
        }

        getNewValue(): Value {
            return this.newValue;
        }
    }
}