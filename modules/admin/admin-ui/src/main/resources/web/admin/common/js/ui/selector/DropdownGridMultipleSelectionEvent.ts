module api.ui.selector {

    export class DropdownGridMultipleSelectionEvent {

        private rows: number[];

        constructor(rows: number[]) {
            this.rows = rows;
        }

        getRows(): number[] {
            return this.rows;
        }

    }
}
