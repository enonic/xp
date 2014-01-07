module app.wizard {

    export class SiteTemplateWizardPanel extends api.app.wizard.WizardPanel<any> {

        private static DEFAULT_SITE_TEMPLATE_ICON_URL:string = api.util.getAdminUri("common/images/default.content.png");
        private formIcon:api.app.wizard.FormIcon;
        private wizardHeader:api.app.wizard.WizardHeaderWithDisplayNameAndName;
        private iconUploadId:string;

        private siteTemplateStep:SiteTemplateWizardStepForm;

        constructor(tabId:api.app.AppBarTabId )
        {
            this.wizardHeader = new api.app.wizard.WizardHeaderWithDisplayNameAndNameBuilder().build();
            var actions = new SiteTemplateWizardActions(this);

            var iconUrl = SiteTemplateWizardPanel.DEFAULT_SITE_TEMPLATE_ICON_URL;
            this.formIcon = new api.app.wizard.FormIcon(iconUrl, "Click to upload icon",
                                                        api.util.getRestUri("upload"));

            this.formIcon.addListener({

                    onUploadFinished: (uploadItem:api.ui.UploadItem) => {

                        this.iconUploadId = uploadItem.getName();
                        this.formIcon.setSrc(api.util.getRestUri('upload/' + uploadItem.getName()));
                    }
            });

            var mainToolbar = new SiteTemplateWizardToolbar({
                    saveAction: actions.getSaveAction(),
                    duplicateAction: actions.getDuplicateAction(),
                    moveAction: actions.getMoveAction(),
                    deleteAction: actions.getDeleteAction(),
                    closeAction: actions.getCloseAction()
                });

            var stepToolbar = new api.ui.toolbar.Toolbar();
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

        createSteps():api.app.wizard.WizardStep[] {
            var steps:api.app.wizard.WizardStep[] = [];
            this.siteTemplateStep = new SiteTemplateWizardStepForm();
            steps.push(new api.app.wizard.WizardStep("Site Template", this.siteTemplateStep));
            steps.push(new api.app.wizard.WizardStep("Content", new ContentWizardStepForm()));
            steps.push(new api.app.wizard.WizardStep("Components", new ComponentsWizardStepForm()));
            steps.push(new api.app.wizard.WizardStep("Summary", new SummaryWizardStepForm()));
            return steps;
        }

        renderNew(): Q.Promise<void>  {

            var deferred = Q.defer<void>();
            super.renderNew().
                done(() => {

                this.siteTemplateStep.renderNew();
                deferred.resolve(null);
            });

            return deferred.promise;
        }
    }
}