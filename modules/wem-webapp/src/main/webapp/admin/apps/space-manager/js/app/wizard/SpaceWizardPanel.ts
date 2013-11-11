module app_wizard {

    export class SpaceWizardPanel extends api_app_wizard.WizardPanel<api_remote_space.Space> {

        public static NEW_WIZARD_HEADER = "New Space";

        private static DEFAULT_SPACE_ICON_URL:string = api_util.getRestUri("space/image/_");

        private formIcon:api_app_wizard.FormIcon;

        private spaceWizardHeader:api_app_wizard.WizardHeaderWithDisplayNameAndName;

        private spaceForm:SpaceForm;

        private schemaPanel:api_ui.Panel;

        private modulesPanel:api_ui.Panel;

        private templatesPanel:api_ui.Panel;

        private persistedSpace:api_remote_space.Space;

        constructor(tabId:api_app.AppBarTabId) {

            this.spaceWizardHeader = new api_app_wizard.WizardHeaderWithDisplayNameAndName();
            this.formIcon =
            new api_app_wizard.FormIcon(SpaceWizardPanel.DEFAULT_SPACE_ICON_URL, "Click to upload icon",
                api_util.getRestUri("upload"));

            var actions = new SpaceWizardActions(this);

            var mainToolbar = new SpaceWizardToolbar({
                saveAction: actions.getSaveAction(),
                duplicateAction: actions.getDuplicateAction(),
                deleteAction: actions.getDeleteAction(),
                closeAction: actions.getCloseAction()
            });

            super({
                tabId: tabId,
                formIcon: this.formIcon,
                mainToolbar: mainToolbar,
                actions: actions,
                header: this.spaceWizardHeader
            });

            this.spaceWizardHeader.initNames(SpaceWizardPanel.NEW_WIZARD_HEADER, null);
            this.spaceWizardHeader.setAutogenerateName(true);

            this.spaceForm = new SpaceForm();

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

            this.addStep(new api_app_wizard.WizardStep("Space"), this.spaceForm);
            this.addStep(new api_app_wizard.WizardStep("Schemas"), this.schemaPanel);
            this.addStep(new api_app_wizard.WizardStep("Modules"), this.modulesPanel);
            this.addStep(new api_app_wizard.WizardStep("Templates"), this.templatesPanel);
        }

        setPersistedItem(space:api_remote_space.Space) {
            super.setPersistedItem(space);

            this.spaceWizardHeader.initNames(space.displayName, space.name);
            // setup displayName and name to be generated automatically
            // if corresponding values are empty
            this.spaceWizardHeader.setAutogenerateName(!space.name);

            this.formIcon.setSrc(space.iconUrl);
            this.persistedSpace = space;
        }

        persistNewItem(successCallback?:() => void) {

            var createParams:api_remote_space.CreateParams = {
                spaceName: this.spaceWizardHeader.getName(),
                displayName: this.spaceWizardHeader.getDisplayName(),
                iconReference: this.getIconUrl()
            };

            api_remote_space.RemoteSpaceService.space_createOrUpdate(createParams, () => {

                new app_wizard.SpaceCreatedEvent().fire();
                api_notify.showFeedback('Space was created!');

                if (successCallback) {
                    successCallback.call(this);
                }
            });
        }

        updatePersistedItem(successCallback?:() => void) {

            var updateParams:api_remote_space.UpdateParams = {
                spaceName: this.persistedSpace.name,
                newSpaceName: this.spaceWizardHeader.getName(),
                displayName: this.spaceWizardHeader.getDisplayName(),
                iconReference: this.getIconUrl()
            };

            api_remote_space.RemoteSpaceService.space_createOrUpdate(updateParams, () => {

                new app_wizard.SpaceUpdatedEvent().fire();
                api_notify.showFeedback('Space was saved!');

                if (successCallback) {
                    successCallback.call(this);
                }
            });
        }
    }
}