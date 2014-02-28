module api.ui.combobox {

    export class ComboBoxValueChangedEvent<T> {

        private oldValue: string;

        private newValue: string;

        private grid: api.ui.grid.Grid<Option<T>>;

        constructor(oldValue: string, newValue: string, grid: api.ui.grid.Grid<Option<T>>) {
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

        getGrid(): api.ui.grid.Grid<Option<T>> {
            return this.grid;
        }
    }
}