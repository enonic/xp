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

        getIconUrl():string {
            return null; // TODO:
        }

        getDisplayName():string{
            return this.wizardPanelHeader.getDisplayName();
        }

        setDisplayName(value:string) {
            this.wizardPanelHeader.setDisplayName(value);
        }

        setName(value:string) {
            this.wizardPanelHeader.setName(value);
        }

        getName():string{
            return this.wizardPanelHeader.getName();
        }

        addStep(step:WizardStep) {
            this.steps.push(step);
            this.stepContainer.addStep(step);
        }


        canClose():bool {

            if (this.hasUnsavedChanges()) {
                this.askUserForSaveChangesBeforeClosing();
                return false;
            }
            else {
                return true;
            }
        }

        /*
         * Override this method in specific wizard to do proper check.
         */
        hasUnsavedChanges():bool {
            return false;
        }

        askUserForSaveChangesBeforeClosing() {
            // TODO: You have unsaved changes - do you want to save before closing?
        }

        /*
         * Override this method in specific wizard to actual saving of changes.
         */
        saveChanges() {
            // TODO
        }
    }

    export class WizardPanelHeader extends api_dom.DivEl {

        private displayNameEl:api_dom.Element;

        private nameEl:api_dom.Element;

        constructor() {
            super(null, "header");
            this.displayNameEl = new api_dom.Element("input", null, "displayName");
            new api_ui.Tooltip(this.displayNameEl, "Display name", 100, api_ui.Tooltip.TRIGGER_FOCUS, api_ui.Tooltip.SIDE_RIGHT, [7,0]);
            this.appendChild(this.displayNameEl);
            this.nameEl = new api_dom.Element("input", null, "name");
            new api_ui.Tooltip(this.nameEl, "Name", 100, api_ui.Tooltip.TRIGGER_FOCUS, api_ui.Tooltip.SIDE_RIGHT, [7,0]);
            this.appendChild(this.nameEl);
        }

        getDisplayName():string {
            return this.displayNameEl.getEl().getInnerHtml();
        }

        setDisplayName(value:string) {
            this.displayNameEl.getEl().setValue(value);
        }

        getName():string {
            return this.nameEl.getEl().getInnerHtml();
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