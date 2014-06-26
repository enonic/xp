module api.ui.grid {

    export class GridOnClickDataBuilder {

        row: number;

        cell: number;

        grid: any;

        constructor(source?: GridOnClickData) {

            if (source) {
                this.row = source.getRow();
                this.cell = source.getCell();
                this.grid = source.getGrid();
            }
        }

        setRow(row: number): GridOnClickDataBuilder {
            this.row = row;
            return this;
        }

        setCell(cell: number): GridOnClickDataBuilder {
            this.cell = cell;
            return this;
        }

        setGrid(grid: any): GridOnClickDataBuilder {
            this.grid = grid;
            return this;
        }

        build(): GridOnClickData {
            return new GridOnClickData(this);
        }
    }

    export class GridOnClickData implements Slick.OnClickEventData {

        row: number;

        cell: number;

        grid: any;

        constructor(builder: GridOnClickDataBuilder) {
            this.row = builder.row;
            this.cell = builder.cell;
            this.grid = builder.grid;
        }

        getRow(): number {
            return this.row;
        }

        getCell(): number {
            return this.cell;
        }

        getGrid(): any {
            return this.grid;
        }
    }
}