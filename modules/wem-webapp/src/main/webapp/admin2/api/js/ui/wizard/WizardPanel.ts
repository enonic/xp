module api_ui_wizard {
    export class WizardPanel extends api_ui.Panel {
        private steps:WizardStep[] = [];
        private stepContainer:WizardStepContainer;
        ext;

        constructor() {
            super("wizard-panel");
            this.addStepContainer();
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
            this.appendChild(titleEl);
        }

        setSubtitle(subtitle:string) {
            var subTitleEl = new api_ui.Element("input", "title");
            subTitleEl.getEl().setValue(subtitle);
            this.appendChild(subTitleEl);
        }

        addStep(step:WizardStep) {
            this.steps.push(step);
            this.stepContainer.addStep(step);
        }

        private addStepContainer() {
            var stepContainerEl = new WizardStepContainer();
            this.stepContainer = stepContainerEl;
            this.appendChild(stepContainerEl);
        }

    }

    class WizardStepContainer extends api_ui.UlEl {
        constructor() {
            super("step-container");
            this.getEl().addClass("step-container");
        }

        addStep(step:WizardStep) {
            var stepEl = new api_ui.LiEl(step.getLabel());
            stepEl.getEl().setInnerHtml(step.getLabel());
            this.appendChild(stepEl);
        }
    }

    export class WizardStep {
        private label;
        private panel;

        constructor(label:string, panel:api_ui.Panel) {
            this.label = label;
            this.panel = panel;
        }

        getLabel():string {
            return this.label;
        }

        getPanel():api_ui.Panel {
            return this.panel;
        }
    }
}