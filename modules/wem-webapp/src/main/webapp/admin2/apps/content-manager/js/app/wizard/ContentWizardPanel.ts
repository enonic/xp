module app_wizard {

    export class ContentWizardPanel extends api_app_wizard.WizardPanel {

        private static DEFAULT_CONTENT_ICON_URL:string = "resources/images/icons/128x128/default_content.png";

        private closeAction:api_ui.Action;

        private saveAction:api_ui.Action;

        private duplicateAction:api_ui.Action;

        private deleteAction:api_ui.Action;

        private formIcon:api_app_wizard.FormIcon;

        private toolbar:ContentWizardToolbar;

        private contentForm:ContentForm;

        private schemaPanel:api_ui.Panel;

        private modulesPanel:api_ui.Panel;

        private templatesPanel:api_ui.Panel;

        constructor(id:string) {

            this.formIcon = new api_app_wizard.FormIcon(ContentWizardPanel.DEFAULT_CONTENT_ICON_URL, "Click to upload icon", "rest/upload");

            this.closeAction = new CloseContentPanelAction(this, true);
            this.saveAction = new SaveContentAction();

            this.duplicateAction = new DuplicateContentAction();
            this.deleteAction = new DeleteContentAction();

            this.toolbar = new ContentWizardToolbar({
                saveAction: this.saveAction,
                duplicateAction: this.duplicateAction,
                deleteAction: this.deleteAction,
                closeAction: this.closeAction
            });

            super({
                formIcon: this.formIcon,
                toolbar: this.toolbar,
                saveAction: this.saveAction
            });

            this.setDisplayName("New Content");
            this.setName(id);

            this.contentForm = new ContentForm();

            this.schemaPanel = new api_ui.Panel("schemaPanel");
            var h1El = new api_dom.H1El();
            h1El.getEl().setInnerHtml("TODO: schema");
            this.schemaPanel.appendChild(h1El);

            this.modulesPanel = new api_ui.Panel("modulesPanel");
            h1El = new api_dom.H1El();
            h1El.getEl().setInnerHtml("TODO: modules");
            this.modulesPanel.appendChild(h1El);

            this.templatesPanel = new api_ui.Panel("templatesPanel");
            h1El = new api_dom.H1El();
            h1El.getEl().setInnerHtml("TODO: templates");
            this.templatesPanel.appendChild(h1El);

            this.addStep(new api_app_wizard.WizardStep("Content", this.contentForm));
            this.addStep(new api_app_wizard.WizardStep("Schemas", this.schemaPanel));
            this.addStep(new api_app_wizard.WizardStep("Modules", this.modulesPanel));
            this.addStep(new api_app_wizard.WizardStep("Templates", this.templatesPanel));
        }

        setData(result:api_remote.RemoteCallContentGetResult) {

            this.setDisplayName(result.content[0].displayName);
            this.setName(result.content[0].name);
            this.formIcon.setSrc(result.content[0].iconUrl);
        }

        saveChanges() {

            // TODO
        }
    }
}