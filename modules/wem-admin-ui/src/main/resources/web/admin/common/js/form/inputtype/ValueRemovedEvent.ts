module api.form.inputtype {

    import Value = api.data2.Value;

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