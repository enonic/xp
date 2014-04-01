module api.form.inputtype {

    import Value = api.data.Value;

    export class ValueRemovedEvent {

        private arrayIndex: number;

        constructor(arrayIndex: number) {
            this.arrayIndex = arrayIndex;
        }

        getArrayIndex(): number {
            return this.arrayIndex;
        }
    }
}