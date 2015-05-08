module app.publish {

    import BrowseItem = api.app.browse.BrowseItem;

    export class SelectionPublishItem<M extends api.Equitable> extends api.dom.DivEl {

        private viewer: api.ui.Viewer<M>;

        private item: BrowseItem<M>;

        private checkBox: api.ui.Checkbox;

        private statusDiv: api.dom.DivEl;

        private hidden: boolean;

        private resolvedContext: ResolvedPublishContext;

        constructor(viewer: api.ui.Viewer<M>, item: BrowseItem<M>, status: string, resolvedContext: ResolvedPublishContext,
                    changeCallback?: () => void,
                    isCheckBoxEnabled: boolean = true, isHidden: boolean = false, isChecked: boolean = true) {
            super("browse-selection-publish-item");
            this.viewer = viewer;
            this.item = item;
            this.hidden = isHidden;
            this.resolvedContext = resolvedContext;
            this.initStatusDiv(status);
            this.initCheckBox(isCheckBoxEnabled, isChecked, changeCallback);
        }

        private initStatusDiv(status: string) {
            this.statusDiv = new api.dom.DivEl("status");
            this.statusDiv.setHtml(status);
        }


        private initCheckBox(isCheckBoxEnabled: boolean, isChecked: boolean, callback?: () => void) {
            this.checkBox = new api.ui.Checkbox();
            this.checkBox.addClass("checkbox publish")
            this.checkBox.setChecked(isChecked);
            if (!isCheckBoxEnabled) {
                this.checkBox.setDisabled(true);
                this.addClass("disabled");
            } else {
                this.checkBox.onValueChanged((event: api.ui.ValueChangedEvent) => {
                    if (callback) {
                        callback();
                    }
                });
            }
        }

        getResolvedContext(): ResolvedPublishContext {
            return this.resolvedContext;
        }

        getBrowseItem(): BrowseItem<M> {
            return this.item;
        }

        isChecked(): boolean {
            return this.checkBox.isChecked();
        }

        isHidden(): boolean {
            return this.hidden;
        }

        setChecked(value: boolean) {
            this.checkBox.setChecked(value);
        }

        updateViewer(viewer: api.ui.Viewer<M>) {
            this.viewer = viewer;
            this.render();
        }

        doRender(): boolean {
            this.removeChildren();
            this.appendChild(this.checkBox);
            this.appendChild(this.viewer);
            this.appendChild(this.statusDiv);

            return true;
        }
    }

}
