module api.form.inputtype.support {

    export class ValueChangedEvent {

        private oldValue:api.data.Value;

        private newValue:api.data.Value;

        constructor(oldValue:api.data.Value, newValue:api.data.Value) {
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        getOldValue():api.data.Value {
            return this.oldValue;
        }

        getNewValue():api.data.Value {
            return this.newValue;
        }
    }
}