module app_wizard {

    export class SpaceWizardPanel2 extends api_wizard.WizardPanel {

        private static DEFAULT_SPACE_ICON_URL:string = "resources/images/icons/128x128/default_space.png";

        private formIcon:api_wizard.FormIcon;

        private toolbar:SpaceWizardToolbar2;

        private spacePanel:api_ui.Panel;

        private schemaPanel:api_ui.Panel;

        private modulesPanel:api_ui.Panel;

        private templatesPanel:api_ui.Panel;

        constructor(id:string) {

            var context = SpaceWizardContext.createSpaceWizardContext();

            this.formIcon = new api_wizard.FormIcon(SpaceWizardPanel2.DEFAULT_SPACE_ICON_URL, "Click to upload icon", "rest/upload");
            this.toolbar = new SpaceWizardToolbar2(context.getActions());

            super({
                formIcon: this.formIcon,
                toolbar: this.toolbar
            });

            this.setDisplayName("New Space");
            this.setName(id);

            this.spacePanel = new api_ui.Panel("spacePanel");
            var h1El = new api_dom.H1El();
            h1El.getEl().setInnerHtml("space");
            this.spacePanel.appendChild(h1El);

            this.schemaPanel = new api_ui.Panel("schemaPanel");
            h1El = new api_dom.H1El();
            h1El.getEl().setInnerHtml("schema");
            this.schemaPanel.appendChild(h1El);

            this.modulesPanel = new api_ui.Panel("modulesPanel");
            h1El = new api_dom.H1El();
            h1El.getEl().setInnerHtml("modules");
            this.modulesPanel.appendChild(h1El);

            this.templatesPanel = new api_ui.Panel("templatesPanel");
            h1El = new api_dom.H1El();
            h1El.getEl().setInnerHtml("templates");
            this.templatesPanel.appendChild(h1El);

            this.addStep(new api_wizard.WizardStep("Space", this.spacePanel));
            this.addStep(new api_wizard.WizardStep("Schemas", this.schemaPanel));
            this.addStep(new api_wizard.WizardStep("Modules", this.modulesPanel));
            this.addStep(new api_wizard.WizardStep("Templates", this.templatesPanel));
        }

        setData(result:api_remote.RemoteCallSpaceGetResult) {
            this.setDisplayName(result.space.displayName);
            this.setName(result.space.name);
            this.formIcon.setSrc(result.space.iconUrl);
        }
    }
}