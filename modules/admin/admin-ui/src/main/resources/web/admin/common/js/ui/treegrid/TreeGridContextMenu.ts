module api.ui.treegrid {

    import TreeGridActions = api.ui.treegrid.actions.TreeGridActions;

    export class TreeGridContextMenu extends api.ui.menu.ContextMenu {

        private actions: TreeGridActions<any>;

        constructor(actions: TreeGridActions<any>) {
            super();

            this.actions = actions;
            this.addActions(actions.getAllActions());
        }

        getActions(): TreeGridActions<any> {
            return this.actions;
        }

        showAt(x: number, y: number) {
            TreeGridContextMenu.prototype.doMoveTo(this, this.restrainX(x), this.restrainY(y));
            this.show();
        }

        private restrainX(x: number): number {
            let posX = x;
            let width = this.getEl().getWidthWithBorder();
            let right = this.getParentElement().getEl().getWidthWithMargin();

            if (posX + width > right) {
                posX = posX - width;
            }

            return posX;
        }

        private restrainY(y: number): number {
            let posY = y;
            let height = this.getEl().getHeightWithBorder();
            let bottom = this.getParentElement().getEl().getHeightWithBorder();

            if (posY + height > bottom) {
                posY = posY - height;
            }

            return posY;
        }
    }
}
