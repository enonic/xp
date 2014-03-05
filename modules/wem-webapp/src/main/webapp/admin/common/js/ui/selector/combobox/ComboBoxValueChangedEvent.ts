module api.ui.selector.combobox {

    export class ComboBoxValueChangedEvent<T> {

        private oldValue: string;

        private newValue: string;

        private grid: api.ui.grid.Grid<api.ui.selector.Option<T>>;

        constructor(oldValue: string, newValue: string, grid: api.ui.grid.Grid<api.ui.selector.Option<T>>) {
            this.oldValue = oldValue;
            this.newValue = newValue;
            this.grid = grid;
        }

        getOldValue(): string {
            return this.oldValue;
        }

        getNewValue(): string {
            return this.newValue;
        }

        getGrid(): api.ui.grid.Grid<api.ui.selector.Option<T>> {
            return this.grid;
        }
    }
}