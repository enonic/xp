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
        }

        private initRemoveButton(callback?: () => void) {
            this.removeEl = new api.dom.DivEl("icon remove");
            this.removeEl.onClicked((event: MouseEvent) => {
                if (callback) {
                    callback();
                }
            });
        }

        getBrowseItem(): BrowseItem<M> {
            return this.item;
        }

        updateViewer(viewer: api.ui.Viewer<M>) {
            this.viewer = viewer;
            this.render();
        }

        doRender(): boolean {
            this.removeChildren();
            this.appendChild(this.removeEl);
            this.appendChild(this.viewer);

            return true;
        }
    }

}
