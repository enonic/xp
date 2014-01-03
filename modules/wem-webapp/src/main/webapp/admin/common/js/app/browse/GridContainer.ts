module api.app.browse {

    export class GridContainer extends api.ui.Panel {

        private grid:api.app.browse.grid.TreeGridPanel;

        constructor(grid:api.app.browse.grid.TreeGridPanel) {
            super("GridContainer");
            this.setScrollY();
            this.addClass("grid-container");
            this.grid = grid;
        }

        afterRender() {
            this.grid.create('center', this.getId());
            super.afterRender();
        }
    }
}