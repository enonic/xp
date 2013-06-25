module app_wizard {

    export class SpaceWizardPanel2 extends api_wizard.WizardPanel {

        private static DEFAULT_SPACE_ICON_URL:string = "resources/images/icons/128x128/default_space.png";

        constructor(id:string, title:string, iconUrl?:string) {
            super();

            if( iconUrl == null ) {
                iconUrl = SpaceWizardPanel2.DEFAULT_SPACE_ICON_URL;
            }

            var context = SpaceWizardContext.createSpaceWizardContext();
            this.setTitle(title);
            this.setSubtitle(id);


            var spacePanel = new api_ui.Panel("spacePanel");
            var h1El = new api_dom.H1El();
            h1El.getEl().setInnerHtml("space");
            spacePanel.appendChild(h1El);

            var schemaPanel = new api_ui.Panel("schemaPanel");
            h1El = new api_dom.H1El();
            h1El.getEl().setInnerHtml("schema");
            schemaPanel.appendChild(h1El);

            var modulesPanel = new api_ui.Panel("modulesPanel");
            h1El = new api_dom.H1El();
            h1El.getEl().setInnerHtml("modules");
            modulesPanel.appendChild(h1El);

            var templatesPanel = new api_ui.Panel("templatesPanel");
            h1El = new api_dom.H1El();
            h1El.getEl().setInnerHtml("templates");
            templatesPanel.appendChild(h1El);

            this.addStep(new api_wizard.WizardStep("Space", spacePanel));
            this.addStep(new api_wizard.WizardStep("Schemas", schemaPanel));
            this.addStep(new api_wizard.WizardStep("Modules", modulesPanel));
            this.addStep(new api_wizard.WizardStep("Templates", templatesPanel));

            this.addToolbar(new SpaceWizardToolbar2(context.getActions()));
            this.addIcon(new api_wizard.FormIcon(iconUrl, "Click to upload icon", "rest/upload"))
        }
    }
}