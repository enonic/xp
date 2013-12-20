module app_wizard {

    export class SiteTemplateWizardPanel extends api_app_wizard.WizardPanel<any> {

        private static DEFAULT_SITE_TEMPLATE_ICON_URL:string = api_util.getAdminUri("common/images/default_content.png");
        private formIcon:api_app_wizard.FormIcon;
        private wizardHeader:api_app_wizard.WizardHeaderWithDisplayNameAndName;
        private iconUploadId:string;

        private siteTemplateStep:SiteTemplateWizardStepForm;

        constructor(tabId:api_app.AppBarTabId )
        {
            this.wizardHeader = new api_app_wizard.WizardHeaderWithDisplayNameAndNameBuilder().build();
            var actions = new SiteTemplateWizardActions(this);

            var iconUrl = SiteTemplateWizardPanel.DEFAULT_SITE_TEMPLATE_ICON_URL;
            this.formIcon = new api_app_wizard.FormIcon(iconUrl, "Click to upload icon",
                                                        api_util.getRestUri("upload"));

            this.formIcon.addListener({

                    onUploadFinished: (uploadItem:api_ui.UploadItem) => {

                        this.iconUploadId = uploadItem.getName();
                        this.formIcon.setSrc(api_util.getRestUri('upload/' + uploadItem.getName()));
                    }
            });

            var mainToolbar = new SiteTemplateWizardToolbar({
                    saveAction: actions.getSaveAction(),
                    duplicateAction: actions.getDuplicateAction(),
                    moveAction: actions.getMoveAction(),
                    deleteAction: actions.getDeleteAction(),
                    closeAction: actions.getCloseAction()
                });

            var stepToolbar = new api_ui_toolbar.Toolbar();
            stepToolbar.addAction(actions.getSaveAction());
            super({
                tabId: tabId,
                persistedItem: null,
                formIcon: this.formIcon,
                mainToolbar: mainToolbar,
                stepToolbar: stepToolbar,
                header: this.wizardHeader,
                actions: actions,
                steps: this.createSteps()
            }, () => {});
        }

        createSteps():api_app_wizard.WizardStep[] {
            var steps:api_app_wizard.WizardStep[] = [];
            this.siteTemplateStep = new SiteTemplateWizardStepForm();
            steps.push(new api_app_wizard.WizardStep("Site Template", this.siteTemplateStep));
            steps.push(new api_app_wizard.WizardStep("Content", new ContentWizardStepForm()));
            steps.push(new api_app_wizard.WizardStep("Components", new ComponentsWizardStepForm()));
            steps.push(new api_app_wizard.WizardStep("Summary", new SummaryWizardStepForm()));
            return steps;
        }

        renderNew() {
            super.renderNew(() => {});
            this.siteTemplateStep.renderNew();
        }
    }
}