module api.ui.button {

    export class ActionButton extends api.ui.button.Button {

        private action: Action;
        private tooltip: Tooltip;

        constructor(action: Action, showTooltip: boolean = true) {
            super();

            this.action = action;
            this.setLabel(this.createLabel(action), false);
            this.addClass('action-button');

            this.setEnabled(this.action.isEnabled());
            this.setVisible(this.action.isVisible());

            if (this.action.getIconClass()) {
                this.addClass(action.getIconClass());
            }

            if (this.action.hasShortcut() && showTooltip) {
                let combination = this.action.getShortcut().getCombination();
                if (combination) {
                    combination = combination.replace(/mod\+/i, BrowserHelper.isOSX() || BrowserHelper.isIOS() ? 'cmd+' : 'ctrl+');
                }
                this.tooltip = new Tooltip(this, combination, 1000);
                api.ui.KeyBindings.get().onHelpKeyPressed((e) => {
                    if (this.action.isEnabled() && api.ui.KeyBindings.get().isActive(this.action.getShortcut())) {
                        if (KeyBindingAction[KeyBindingAction.KEYDOWN].toLowerCase() === e.type) {
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

            this.action.onPropertyChanged((changedAction: api.ui.Action) => {
                this.setEnabled(changedAction.isEnabled());
                this.setVisible(changedAction.isVisible());
                this.setLabel(this.createLabel(changedAction), false);
            });
        }

        getAction(): Action {
            return this.action;
        }

        getTooltip(): Tooltip {
            return this.tooltip;
        }

        private createLabel(action: Action): string {
            let label: string;
            if (action.hasMnemonic()) {
                label = action.getMnemonic().underlineMnemonic(action.getLabel());
            } else {
                label = action.getLabel();
            }
            return label;
        }

    }
}
