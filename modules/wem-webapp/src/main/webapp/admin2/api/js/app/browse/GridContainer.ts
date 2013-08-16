module api_app_browse {

    export class GridContainer extends api_ui.Panel {

        private grid:api_ui_grid.TreeGridPanel;

        constructor(grid:api_ui_grid.TreeGridPanel) {
            super("GridContainer");
            this.setScrollY();
            this.addClass("grid-container");
            this.grid = grid;
        }

        afterRender() {
            this.grid.create('center', this.getId());
        }
    }
}