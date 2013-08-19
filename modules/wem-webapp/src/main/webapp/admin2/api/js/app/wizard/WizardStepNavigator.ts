module api_app_wizard {

    export interface WizardStepNavigatorListener extends api_ui.Listener {

        onStepShown?(step:WizardStep);

    }

    export class WizardStepNavigator extends api_dom.UlEl implements api_ui.Observable {

        private deckPanel:api_app_wizard.WizardStepDeckPanel;

        private steps:WizardStep[] = [];

        private activeStepIndex:number;

        private listeners:WizardStepNavigatorListener[] = [];

        constructor(deckPanel:api_app_wizard.WizardStepDeckPanel) {
            super("WizardStepNavigator", "step-navigator");
            this.deckPanel = deckPanel;
        }

        addStep(step:WizardStep) {
            this.steps.push(step);
            var panelIndex = this.deckPanel.addPanel(step.getPanel());


            var stepEl = new api_dom.LiEl(step.getLabel());
            step.setEl(stepEl);
            stepEl.getEl().setInnerHtml(step.getLabel());
            stepEl.getEl().addEventListener("click", (event) => {
                this.showStep(step);
            });
            if (this.steps.length == 1) {
                step.setActive(true);
                stepEl.getEl().addClass("first");
            }
            step.setIndex(panelIndex);

            if (panelIndex == 0) {
                this.showStep(step);
            }
            this.appendChild(stepEl);
            this.notifyStepShown(step);
        }

        showStep(step:WizardStep) {
            this.removeActive();
            step.setActive(true);
            this.deckPanel.showPanel(step.getIndex());
            this.activeStepIndex = step.getIndex();
            this.notifyStepShown(step);
        }

        nextStep() {
            var step;
            if (this.activeStepIndex >= this.steps.length - 1) {
                step = this.steps[this.steps.length - 1];
            } else {
                step = this.steps[this.activeStepIndex + 1];
            }
            this.showStep(step);
        }

        previousStep() {
            var step;
            if (this.activeStepIndex == 0) {
                step = this.steps[0];
            } else {
                step = this.steps[this.activeStepIndex - 1];
            }
            this.showStep(step);
        }

        hasNext():bool {
            if (this.steps) {
                return this.activeStepIndex < this.steps.length - 1;
            }
            return false;
        }

        hasPrevious():bool {
            if (this.steps) {
                return this.activeStepIndex && this.activeStepIndex != 0;
            }
            return false;
        }

        private removeActive() {
            this.steps.forEach((step:WizardStep) => {
                step.setActive(false);
            })
        }

        addListener(listener:WizardStepNavigatorListener) {
            this.listeners.push(listener);
        }

        removeListener(listener:WizardStepNavigatorListener) {
            this.listeners = this.listeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyStepShown(step:WizardStep) {
            this.listeners.forEach((listener:WizardStepNavigatorListener) => {
                if (listener.onStepShown) {
                    listener.onStepShown(step);
                }
            });
        }
    }
}