module api.ui.button {

    export class ActionButton extends api.ui.button.Button {

        private action: Action;
        private tooltip: Tooltip;

        constructor(action: Action, showTooltip: boolean = true) {
            this.action = action;

            var label: string;
            if (this.action.hasMnemonic()) {
                label = this.action.getMnemonic().underlineMnemonic(this.action.getLabel());
            }
            else {
                label = this.action.getLabel();
            }
            super(label);
            this.addClass("action-button");

            this.setEnabled(this.action.isEnabled());

            if (this.action.getIconClass()) {
                this.addClass(action.getIconClass());
            }

            if (this.action.hasShortcut() && showTooltip) {
                this.tooltip = new Tooltip(this, this.action.getShortcut().getCombination(), 1000);
                api.ui.KeyBindings.get().onHelpKeyPressed((e) => {
                    if (this.action.isEnabled() && api.ui.KeyBindings.get().isActive(this.action.getShortcut())) {
                        if (KeyBindingAction[KeyBindingAction.KEYDOWN].toLowerCase() == e.type) {
                            this.tooltip.show();
                            return;
                        }
                    }
                    this.tooltip.hide();
                });
            }

            this.onClicked((event: MouseEvent) => {
                this.action.execute();
            });

            this.action.onPropertyChanged((action: api.ui.Action) => {
                this.setEnabled(action.isEnabled());
                this.setVisible(action.isVisible());
            });
        }

        getTooltip(): Tooltip {
            return this.tooltip;
        }

    }
}
