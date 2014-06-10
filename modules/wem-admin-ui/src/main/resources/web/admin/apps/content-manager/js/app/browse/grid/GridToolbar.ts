module app.browse.grid {

    import Grid = api.ui.grid.Grid;
    import ContentSummary = api.content.ContentSummary;

    export class GridToolbar extends api.dom.DivEl {

        constructor(grid:Grid<ContentSummary>) {
            super("toolbar");

            var selectAll = new api.ui.Button("Select All");
            this.appendChild(selectAll);

            var clearSelection = new api.ui.Button("Clear Selection");
            this.appendChild(clearSelection);

            selectAll.onClicked(() => {
                grid.selectAll();
            });

            clearSelection.onClicked(() => {
                grid.clearSelection();
            });
        }
    }
}
