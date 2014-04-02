module api.ui.selector {

    export class OptionFilterInputValueChangedEvent<OPTION_DISPLAY_VALUE> {

        private oldValue: string;

        private newValue: string;

        private grid: api.ui.grid.Grid<api.ui.selector.Option<OPTION_DISPLAY_VALUE>>;

        constructor(oldValue: string, newValue: string, grid: api.ui.grid.Grid<api.ui.selector.Option<OPTION_DISPLAY_VALUE>>) {
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

        getGrid(): api.ui.grid.Grid<api.ui.selector.Option<OPTION_DISPLAY_VALUE>> {
            return this.grid;
        }
    }
}