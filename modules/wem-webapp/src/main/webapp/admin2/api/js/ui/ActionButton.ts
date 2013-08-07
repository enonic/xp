module api_ui{

    export class ActionButton extends api_dom.ButtonEl {

        private action:Action;

        constructor(idPrefix:string, action:Action) {
            super(idPrefix);

            this.action = action;

            this.setEnabled(action.isEnabled());


            if (action.hasMnemonic()) {
                var htmlNodes:Node[] = action.getMnemonic().underlineMnemonic(action.getLabel());
                htmlNodes.forEach((node:Node) => {
                    this.getEl().appendChild(node);
                });
            } else {
                var labelNode = new api_dom.TextNode(action.getLabel());
                this.getEl().appendChild(labelNode.getText());
            }

            this.getEl().addEventListener("click", () => {
                this.action.execute();
            });

            action.addPropertyChangeListener((action:api_ui.Action) => {
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
