module api_app_wizard {

    export interface WizardPanelParams {

        formIcon:FormIcon;

        toolbar:api_ui_toolbar.Toolbar;
    }

    export class WizardPanel extends api_ui.Panel {

        private wizardPanelHeader:WizardPanelHeader;

        private steps:WizardStep[] = [];

        private stepContainer:WizardStepContainer;

        private wizardStepPanels:WizardStepPanels;

        ext;

        constructor(params:WizardPanelParams) {
            super("WizardPanel");

            this.getEl().addClass("wizard-panel");

            this.appendChild(params.toolbar);
            this.appendChild(params.formIcon);

            this.wizardPanelHeader = new WizardPanelHeader();
            this.appendChild(this.wizardPanelHeader);

            this.wizardStepPanels = new WizardStepPanels();
            this.stepContainer = new WizardStepContainer(this.wizardStepPanels);
            this.appendChild(this.stepContainer);
            this.appendChild(this.wizardStepPanels);

            this.initExt();
        }

        private initExt() {
            var htmlEl = this.getHTMLElement();
            this.ext = new Ext.Component({
                contentEl: htmlEl
            });
        }

        setDisplayName(value:string) {
            this.wizardPanelHeader.setDisplayName(value);
        }

        setName(value:string) {
            this.wizardPanelHeader.setName(value);
        }

        addStep(step:WizardStep) {
            this.steps.push(step);
            this.stepContainer.addStep(step);
        }
    }

    export class WizardPanelHeader extends api_dom.DivEl {

        private displayNameEl:api_dom.Element;

        private nameEl:api_dom.Element;

        constructor() {
            super(null, "header");
            this.displayNameEl = new api_dom.Element("input").className("displayName");
            this.appendChild(this.displayNameEl);
            this.nameEl = new api_dom.Element("input").className("name");
            this.appendChild(this.nameEl);
        }

        setDisplayName(value:string) {
            this.displayNameEl.getEl().setValue(value);
        }

        setName(value:string) {
            this.nameEl.getEl().setValue(value);
        }
    }

    export class WizardStepPanels extends api_ui.DeckPanel {
        constructor() {
            super("WizardStepPanels");
        }
    }

    export class WizardStepContainer extends api_dom.UlEl {
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

            var stepEl = new api_dom.LiEl(step.getLabel());
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
        private el:api_dom.Element;

        constructor(label:string, panel:api_ui.Panel) {
            this.label = label;
            this.panel = panel;
        }

        setEl(el:api_dom.Element) {
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

        getEl():api_dom.Element {
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