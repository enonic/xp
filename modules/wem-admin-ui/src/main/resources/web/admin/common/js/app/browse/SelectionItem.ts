module api.app.browse {

    export class SelectionItem<M extends api.Equitable> extends api.dom.DivEl {

        private viewer: api.ui.Viewer<M>;

        constructor(viewer: api.ui.Viewer<M>, removeCallback?: () => void) {
            super("browse-selection-item");
            this.viewer = viewer;
            this.addRemoveButton(removeCallback);
            this.appendChild(this.viewer);
        }

        private addRemoveButton(callback?: () => void) {
            var removeEl = new api.dom.DivEl("icon remove");
            removeEl.onClicked((event: MouseEvent) => {
                if (callback) {
                    callback();
                }
            });
            this.appendChild(removeEl);
        }
    }

}
