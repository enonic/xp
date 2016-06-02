module api.app.browse {

    export class SelectionItem<M extends api.Equitable> extends api.dom.DivEl {

        private viewer: api.ui.Viewer<M>;

        private item: BrowseItem<M>;

        private removeEl: api.dom.DivEl;

        constructor(viewer: api.ui.Viewer<M>, item: BrowseItem<M>, removeCallback?: () => void) {
            super("browse-selection-item");
            this.viewer = viewer;
            this.item = item;
            this.initRemoveButton(removeCallback);
            this.appendChild(this.removeEl);
            this.appendChild(this.viewer);
        }

        private initRemoveButton(callback?: () => void) {
            this.removeEl = new api.dom.DivEl("icon remove");
            this.removeEl.onClicked(() => {
                if (callback) {
                    callback();
                }
            });
        }

        setBrowseItem(item: BrowseItem<M>) {
            this.item = item;
            this.viewer.remove();
            this.viewer.setObject(item.getModel());
            this.appendChild(this.viewer);
        }

        getBrowseItem(): BrowseItem<M> {
            return this.item;
        }

        hideRemoveButton() {
            this.removeEl.hide();
        }

        doRender(): boolean {
            return true;
        }
    }

}
