module api.form {

    export class RemoveButtonClickedEvent<V> {

        private view: V;

        private index: number;

        constructor(view: V, index: number) {
            this.view = view;
            this.index = index;
        }

        getView(): V {
            return this.view;
        }

        getIndex(): number {
            return this.index;
        }
    }
}