module api_ui_wizard {
    export class WizardPanel extends api_ui.Panel {

        private steps:WizardStep[] = [];

        private stepContainer:WizardStepContainer;

        private wizardStepPanels:WizardStepPanels;

        private titleEl:api_ui.Element;
        private subTitleEl:api_ui.Element;

        ext;

        constructor() {
            super("wizard-panel");
            this.getEl().addClass("wizard-panel");
            this.addTitle();
            this.addSubTitle();
            this.wizardStepPanels = new WizardStepPanels();
            this.addStepContainer();
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
            this.titleEl.getEl().setValue(title);
        }

        setSubtitle(subtitle:string) {
            this.subTitleEl.getEl().setValue(subtitle);
        }

        addStep(step:WizardStep) {
            this.steps.push(step);
            this.stepContainer.addStep(step);
        }

        private addTitle() {
            this.titleEl = new api_ui.Element("input", "title");
            this.titleEl.getEl().addClass("title");
            this.appendChild(this.titleEl);
        }

        private addSubTitle() {
            this.subTitleEl = new api_ui.Element("input", "title");
            this.subTitleEl.getEl().addClass("subtitle");
            this.appendChild(this.subTitleEl);
        }

        private addStepContainer() {
            var stepContainerEl = new WizardStepContainer(this.wizardStepPanels);
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
        private deckPanel:WizardStepPanels;
        private steps:WizardStep[] = [];

        constructor(deckPanel:WizardStepPanels) {
            super("step-container", "step-container");
            this.deckPanel = deckPanel;
        }

        addStep(step:WizardStep) {
            this.steps.push(step);
            var panelIndex = this.deckPanel.addPanel(step.getPanel());
            if (panelIndex == 0) {
                this.deckPanel.showPanel(0);
            }

            var stepEl = new api_ui.LiEl(step.getLabel());
            step.setEl(stepEl);
            stepEl.getEl().setInnerHtml(step.getLabel());
            stepEl.getEl().addEventListener("click", (event) => {
                this.removeActive();
                step.setActive(true);
                this.deckPanel.showPanel(panelIndex);
            });
            if (this.steps.length == 1) {
                step.setActive(true);
            }
            this.appendChild(stepEl);
        }

        private removeActive() {
            this.steps.forEach((step:WizardStep) => {
                step.setActive(false);
            })
        }
    }

    export class WizardStep {
        private label:string;
        private panel:api_ui.Panel;
        private active:bool;
        private el:api_ui.Element;

        constructor(label:string, panel:api_ui.Panel) {
            this.label = label;
            this.panel = panel;
        }

        setEl(el:api_ui.Element) {
            this.el = el;
        }

        setActive(active:bool) {
            this.active = active;
            if (active) {
                this.el.getEl().addClass("active");
            } else {
                this.el.getEl().removeClass("active");
            }
        }

        isActive():bool {
            return this.active;
        }

        getEl():api_ui.Element {
            return this.el;
        }

        getLabel():string {
            return this.label;
        }

        getPanel():api_ui.Panel {
            return this.panel;
        }
    }
}