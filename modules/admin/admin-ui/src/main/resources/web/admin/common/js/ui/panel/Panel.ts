module api.ui.panel {

    /**
     * Use Panel when you need a container that needs 100% height.
     */
    export class Panel extends api.dom.DivEl {

        private doOffset: boolean;

        constructor(className?: string) {
            super("panel" + (className ? " " + className : ""));
            this.doOffset = true;

            this.onAdded((event) => {
                if (this.doOffset) {
                    this.calculateOffset();
                }
            });
        }

        setDoOffset(value: boolean) {
            this.doOffset = value;

            if (value && this.isRendered()) {
                this.calculateOffset();
            }
        }

        doRender(): Q.Promise<boolean> {
            return super.doRender().then((rendered) => {
                if (this.doOffset) {
                    this.calculateOffset();
                }

                return rendered;
            });
        }

        protected calculateOffset() {
            // calculates bottom of previous element in dom and set panel top to this value.
            let previous = this.getEl().getPrevious();
            let top = previous ? (previous.getOffsetTopRelativeToParent() + previous.getHeightWithMargin()) : 0;

            this.getEl().setTopPx(top);
        }

        protected resolveActions(panel: api.ui.panel.Panel): api.ui.Action[] {
            let actions = [];
            if (api.ObjectHelper.iFrameSafeInstanceOf(panel, api.app.wizard.WizardPanel) ||
                api.ObjectHelper.iFrameSafeInstanceOf(panel, api.app.browse.BrowsePanel) ||
                api.ObjectHelper.iFrameSafeInstanceOf(panel, api.app.view.ItemViewPanel)) {
                let actionContainer: api.ui.ActionContainer = <any>panel;
                actions = actions.concat(actionContainer.getActions());
            }
            return actions;
        }
    }

}