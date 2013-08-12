module api_ui{

    export class ActionButton extends api_dom.ButtonEl {

        private action:Action;
        private tooltip:Tooltip;

        constructor(idPrefix:string, action:Action, showTooltip:bool = true) {
            super(idPrefix);

            this.action = action;

            if (this.action.getIconClass()) {
                this.getEl().addClass(action.getIconClass());
            }

            this.setEnabled(this.action.isEnabled());

            if (this.action.hasMnemonic()) {
                var htmlNodes:Node[] = this.action.getMnemonic().underlineMnemonic(this.action.getLabel());
                htmlNodes.forEach((node:Node) => {
                    this.getEl().appendChild(node);
                });
            } else {
                var labelNode = new api_dom.TextNode(this.action.getLabel());
                this.getEl().appendChild(labelNode.getText());
            }

            if (this.action.hasShortcut() && showTooltip) {
                this.tooltip = new Tooltip(this, this.action.getShortcut().getCombination(), 1000);
            }

            this.getEl().addEventListener("click", () => {
                this.action.execute();
            });

            this.action.addPropertyChangeListener((action:api_ui.Action) => {
                this.setEnabled(action.isEnabled());
            });
        }

        setEnabled(value:bool) {
            this.getEl().setDisabled(!value);
        }

        isEnabled() {
            return !this.getEl().isDisabled();
        }

    }
}
