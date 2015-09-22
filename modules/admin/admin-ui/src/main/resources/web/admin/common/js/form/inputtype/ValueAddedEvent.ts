module api.form.inputtype {

    import Value = api.data.Value;

    export class ValueAddedEvent {

        private value: Value;

        constructor(value: Value) {
            this.value = value;
        }

        getValue(): Value {
            return this.value;
        }
    }
}