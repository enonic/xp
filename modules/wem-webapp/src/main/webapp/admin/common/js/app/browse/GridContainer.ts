module api.app.browse {

    export class GridContainer extends api.ui.Panel {

        private grid:api.app.browse.grid.TreeGridPanel;

        constructor(grid:api.app.browse.grid.TreeGridPanel) {
            super();
            this.setScrollY();
            this.addClass("grid-container");
            this.grid = grid;

            this.onRendered((event) => {
                this.grid.create('center', this.getId());
            });
        }
    }
}