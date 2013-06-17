module api_ui_wizard {
    export class WizardPanel extends api_ui.Panel {

        private steps:WizardStep[] = [];

        private stepContainer:WizardStepContainer;

        private wizardStepPanels:WizardStepPanels;

        ext;

        constructor() {
            super("wizard-panel");
            this.getEl().addClass("wizard-panel");
            this.addStepContainer();

            this.wizardStepPanels = new WizardStepPanels();
            this.appendChild(this.wizardStepPanels);

            this.initExt();
        }

        private initExt() {
            var htmlEl = this.getHTMLElement();
            this.ext = new Ext.Component({
                contentEl: htmlEl
            });
        }

        setTitle(title:string) {
            var titleEl = new api_ui.Element("input", "title");
            titleEl.getEl().setValue(title);
            titleEl.getEl().addClass("title");
            this.appendChild(titleEl);
        }

        setSubtitle(subtitle:string) {
            var subTitleEl = new api_ui.Element("input", "title");
            subTitleEl.getEl().addClass("subtitle");
            subTitleEl.getEl().setValue(subtitle);
            this.appendChild(subTitleEl);
        }

        addStep(step:WizardStep) {
            this.steps.push(step);
            if (this.steps.length == 1) {
                step.setActive(true);
            }
            this.stepContainer.addStep(step);
        }

        private addStepContainer() {
            var stepContainerEl = new WizardStepContainer();
            this.stepContainer = stepContainerEl;
            this.appendChild(stepContainerEl);
        }

    }

    class WizardStepPanels extends api_ui.DeckPanel {
        constructor() {
            super("WizardStepPanels");
        }
    }

    class WizardStepContainer extends api_ui.UlEl {
        constructor() {
            super("step-container", "step-container");
        }

        addStep(step:WizardStep) {
            var stepEl = new api_ui.LiEl(step.getLabel());
            stepEl.getEl().setInnerHtml(step.getLabel());
            if (step.isActive()) {
                stepEl.getEl().addClass("active");
            }
            this.appendChild(stepEl);
        }
    }

    export class WizardStep {
        private label:string;
        private panel:api_ui.Panel;
        private active:bool;

        constructor(label:string, panel:api_ui.Panel) {
            this.label = label;
            this.panel = panel;
        }

        setActive(active:bool) {
            this.active = active;
        }

        isActive():bool {
            return this.active;
        }

        getLabel():string {
            return this.label;
        }

        getPanel():api_ui.Panel {
            return this.panel;
        }
    }
}