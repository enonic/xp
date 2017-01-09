declare module Slick {

    export class RowSelectionModel<T extends Slick.SlickData, E> extends Slick.SelectionModel<T,E> {

        constructor();

        constructor(options:any);

    }

    export class CheckboxSelectColumn<T extends Slick.SlickData> {

        constructor(options:any);

        public init(grid: Grid<T>): void;

        public destroy(): void;

        getColumnDefinition():Slick.Column<T>;
    }

    export class RowMoveManager<T extends Slick.SlickData> {

        constructor(options: any);

        public init(grid: Grid<T>): void;

        public destroy(): void;

        onBeforeMoveRows: Slick.Event<OnMoveRowsEventData>;

        onMoveRows: Slick.Event<OnMoveRowsEventData>;

    }

    export interface OnMoveRowsEventData {

    }
}
