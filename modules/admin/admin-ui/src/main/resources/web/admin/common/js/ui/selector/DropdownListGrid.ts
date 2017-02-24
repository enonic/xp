module api.ui.selector {

    import Viewer = api.ui.Viewer;
    import Element = api.dom.Element;

    export class DropdownListGrid<OPTION_DISPLAY_VALUE> extends DropdownGrid<OPTION_DISPLAY_VALUE> {

        protected grid: api.ui.grid.Grid<Option<OPTION_DISPLAY_VALUE>>;

        protected gridData: api.ui.grid.DataView<Option<OPTION_DISPLAY_VALUE>>;

        constructor(config: DropdownGridConfig<OPTION_DISPLAY_VALUE>) {
            super(config);
        }

        protected initGridAndData() {
            this.gridData = new api.ui.grid.DataView<Option<OPTION_DISPLAY_VALUE>>();
            if (this.filter) {
                this.gridData.setFilter(this.filter);
            }

            this.grid = new api.ui.grid.Grid<Option<OPTION_DISPLAY_VALUE>>(this.gridData, this.createColumns(), this.createOptions());
        }

        getElement(): Element {
            return this.grid;
        }

        getGrid(): api.ui.grid.Grid<Option<OPTION_DISPLAY_VALUE>> {
            return this.grid;
        }

        protected getGridData(): api.ui.grid.DataView<Option<OPTION_DISPLAY_VALUE>> {
            return this.gridData;
        }
    }
}
