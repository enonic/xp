module app_ui_wizard {
    export class SpaceWizardPanel2 extends api_ui_wizard.WizardPanel {
        constructor(id:string, title:string) {
            super();
            this.setTitle(title);
            this.setSubtitle(id);


            var stepPanel = new api_ui.Panel("spacePanel");

            this.addStep(new api_ui_wizard.WizardStep("Space", stepPanel));
            this.addStep(new api_ui_wizard.WizardStep("Schemas", stepPanel));
            this.addStep(new api_ui_wizard.WizardStep("Modules", stepPanel));
            this.addStep(new api_ui_wizard.WizardStep("Templates", stepPanel));
        }
    }
}