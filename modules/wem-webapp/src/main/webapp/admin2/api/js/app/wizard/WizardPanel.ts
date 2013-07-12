module api_app_wizard {

    export interface WizardPanelParams {

        formIcon:FormIcon;

        toolbar:api_ui_toolbar.Toolbar;

        saveAction:api_ui.Action;
    }

    export class WizardPanel extends api_ui.Panel {

        private persistedItem:api_remote.Item;

        private header:WizardPanelHeader;

        private steps:WizardStep[] = [];

        private stepNavigator:WizardStepNavigator;

        private stepPanels:api_app_wizard.WizardStepDeckPanel;

        // TODO: @alb - Value is set to 'changed' by default to see SaveChangesBeforeCloseDialog behavior.
        private isChanged:bool = true;

        ext;

        constructor(params:WizardPanelParams) {
            super("WizardPanel");

            this.getEl().addClass("wizard-panel");

            this.appendChild(params.toolbar);
            this.appendChild(params.formIcon);

            this.header = new WizardPanelHeader();
            this.appendChild(this.header);

            this.stepPanels = new api_app_wizard.WizardStepDeckPanel();
            this.stepNavigator = new WizardStepNavigator(this.stepPanels);
            this.appendChild(this.stepNavigator);
            // TODO: @alb - Remove comment to display stepPanels. Commented because it breaks wizard layout.
            // this.appendChild(this.stepPanels);

            //this.initExt();

            params.saveAction.addExecutionListener(() => {

                this.saveChanges();
            });
        }

        afterRender() {
            console.log("afterrender wizardPanel");
            super.afterRender();
        }

        private initExt() {
            var htmlEl = this.getHTMLElement();
            this.ext = new Ext.Component({
                contentEl: htmlEl
            });
        }

        setPersistedItem(item:api_remote.Item) {
            this.persistedItem = item;
        }

        isItemPersisted():bool {
            return this.persistedItem != null;
        }

        getIconUrl():string {
            return null; // TODO:
        }

        getDisplayName():string {
            return this.header.getDisplayName();
        }

        setDisplayName(value:string) {
            this.header.setDisplayName(value);
        }

        setName(value:string) {
            this.header.setName(value);
        }

        getName():string {
            return this.header.getName();
        }

        addStep(step:WizardStep) {
            this.steps.push(step);
            this.stepNavigator.addStep(step);
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
            return this.isChanged;
        }

        askUserForSaveChangesBeforeClosing() {
            // TODO: You have unsaved changes - do you want to save before closing?
        }

        saveChanges() {

            if (this.isItemPersisted()) {
                this.updatePersistedItem();
            }
            else {
                this.persistNewItem();
            }

            this.isChanged = false;
        }

        /*
         * Override this method in specific wizard to do actual persisting of new item.
         */
        persistNewItem() {

        }

        /*
         * Override this method in specific wizard to do actual update of item.
         */
        updatePersistedItem() {

        }
    }

    export class WizardPanelHeader extends api_dom.DivEl {

        private displayNameEl:api_dom.Element;

        private nameEl:api_dom.Element;

        constructor() {
            super(null, "header");
            this.displayNameEl = new api_dom.Element("input", null, "displayName");
            new api_ui.Tooltip(this.displayNameEl, "Display name", 100, api_ui.Tooltip.TRIGGER_FOCUS, api_ui.Tooltip.SIDE_RIGHT, [7, 0]);
            this.appendChild(this.displayNameEl);
            this.nameEl = new api_dom.Element("input", null, "name");
            new api_ui.Tooltip(this.nameEl, "Name", 100, api_ui.Tooltip.TRIGGER_FOCUS, api_ui.Tooltip.SIDE_RIGHT, [7, 0]);
            this.appendChild(this.nameEl);
        }

        getDisplayName():string {
            return this.displayNameEl.getEl().getValue();
        }

        setDisplayName(value:string) {
            this.displayNameEl.getEl().setValue(value);
        }

        getName():string {
            return this.nameEl.getEl().getValue();
        }

        setName(value:string) {
            this.nameEl.getEl().setValue(value);
        }
    }

    export class WizardStepDeckPanel extends api_ui.DeckPanel {
        constructor() {
            super("WizardStepDeckPanel");
            this.addClass("step-panel");
            //this.removeClass("panel");
        }

        afterRender() {
            console.log("after render in wizardstepdeckpanel");
            super.afterRender();
        }
    }

    export class WizardStepNavigator extends api_dom.UlEl {

        private deckPanel:api_app_wizard.WizardStepDeckPanel;

        private steps:WizardStep[] = [];

        private activeStepIndex:number;

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
            }
            step.setIndex(panelIndex);

            if (panelIndex == 0) {
                this.showStep(step);
            }
            this.appendChild(stepEl);
        }

        showStep(step:WizardStep) {
            this.removeActive();
            step.setActive(true);
            this.deckPanel.showPanel(step.getIndex());
            this.activeStepIndex = step.getIndex();
        }

        nextStep() {
            var step;
            if (this.activeStepIndex >= this.steps.length) {
                step = this.steps[this.steps.length];
            } else {
                step = this.steps[this.activeStepIndex+1];
            }
            this.showStep(step);
        }

        previousStep() {
            var step;
            if (this.activeStepIndex == 0) {
                step = this.steps[0];
            } else {
                step = this.steps[this.activeStepIndex-1];
            }
            this.showStep(step);
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
        private index:number;

        constructor(label:string, panel:api_ui.Panel) {
            this.label = label;
            this.panel = panel;
        }

        setIndex(index:number) {
            this.index = index;
        }

        getIndex() {
            return this.index;
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