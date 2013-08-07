module api_app_browse_filter {

    export class FilterSearchAction extends api_ui.Action {

        private filterValues:any[];

        constructor() {
            super('filterSearchAction');
        }

        setFilterValues(values:any[]) {
            this.filterValues = values;
        }

        getFilterValues():any[] {
            return this.filterValues;
        }
    }

    export class FilterResetAction extends api_ui.Action {

        constructor() {
            super('filterResetAction');
        }
    }

}