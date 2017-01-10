module api.form {

    export class RemoveButtonClickedEvent<V> {

        private view: V;

        constructor(view: V) {
            this.view = view;
        }

        getView(): V {
            return this.view;
        }

    }
}