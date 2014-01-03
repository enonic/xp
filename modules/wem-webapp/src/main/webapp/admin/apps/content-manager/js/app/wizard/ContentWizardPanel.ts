module app.wizard {

    export class ContentWizardPanelParams {

        createSite: boolean = false;

        tabId: api.app.AppBarTabId;

        contentType: api.schema.content.ContentType;

        parentContent: api.content.Content;

        persistedContent: api.content.Content;

        site: api.content.Content;

        siteTemplate: api.content.site.template.SiteTemplate;

        setAppBarTabId(value: api.app.AppBarTabId): ContentWizardPanelParams {
            this.tabId = value;
            return this;
        }

        setContentType(value: api.schema.content.ContentType): ContentWizardPanelParams {
            this.contentType = value;
            return this;
        }

        setParentContent(value: api.content.Content): ContentWizardPanelParams {
            this.parentContent = value;
            return this;
        }

        setPersistedContent(value: api.content.Content): ContentWizardPanelParams {
            this.persistedContent = value;
            return this;
        }

        setSite(value: api.content.Content): ContentWizardPanelParams {
            this.site = value;
            return this;
        }

        setCreateSite(value: api.content.site.template.SiteTemplate): ContentWizardPanelParams {
            this.siteTemplate = value;
            this.createSite = this.siteTemplate != null;
            return this;
        }

    }

    export class ContentWizardPanel extends api.app.wizard.WizardPanel<api.content.Content> {

        private parentContent: api.content.Content;

        private siteContent: api.content.Content;

        private contentType: api.schema.content.ContentType;

        private formIcon: api.app.wizard.FormIcon;

        private contentWizardHeader: api.app.wizard.WizardHeaderWithDisplayNameAndName;

        private siteWizardStepForm: app.wizard.site.SiteWizardStepForm;

        private contentWizardStepForm: ContentWizardStepForm;

        private pageWizardStepForm: page.PageWizardStepForm;

        private iconUploadItem: api.ui.UploadItem;

        private displayNameScriptExecutor: DisplayNameScriptExecutor;

        private livePanel: LiveFormPanel;

        private persistAsDraft: boolean;

        private createSite: boolean;

        private siteTemplate: api.content.site.template.SiteTemplate;

        constructor(params: ContentWizardPanelParams, callback: (wizard: ContentWizardPanel) => void) {

            console.log("ContentWizardPanel.constructor started");

            this.persistAsDraft = true;
            this.parentContent = params.parentContent;
            this.siteContent = params.site;
            this.contentType = params.contentType;
            this.displayNameScriptExecutor = new DisplayNameScriptExecutor();
            this.contentWizardHeader = new api.app.wizard.WizardHeaderWithDisplayNameAndNameBuilder().
                setDisplayNameGenerator(this.displayNameScriptExecutor).
                build();

            var iconUrl = api.content.ContentIconUrlResolver.default();
            this.formIcon = new api.app.wizard.FormIcon(iconUrl, "Click to upload icon",
                api.util.getRestUri("blob/upload"));

            this.formIcon.addListener({

                onUploadFinished: (uploadItem: api.ui.UploadItem) => {

                    this.iconUploadItem = uploadItem;
                    this.formIcon.setSrc(api.util.getRestUri('blob/' + this.iconUploadItem.getBlobKey()));
                }
            });

            var actions = new app.wizard.action.ContentWizardActions(this);

            var mainToolbar = new ContentWizardToolbar({
                saveAction: actions.getSaveAction(),
                duplicateAction: actions.getDuplicateAction(),
                deleteAction: actions.getDeleteAction(),
                closeAction: actions.getCloseAction(),
                publishAction: actions.getPublishAction()
            });

            var stepToolbar = new api.ui.toolbar.Toolbar();

            this.livePanel = new LiveFormPanel(this.siteContent);

            if (this.parentContent) {
                this.contentWizardHeader.setPath(this.parentContent.getPath().toString() + "/");
            } else {
                this.contentWizardHeader.setPath("/");
            }

            this.createSite = params.createSite;
            this.siteTemplate = params.siteTemplate;
            if (this.createSite || params.persistedContent != null && params.persistedContent.isSite()) {
                this.siteWizardStepForm = new app.wizard.site.SiteWizardStepForm();

            }
            else {
                this.siteWizardStepForm = null;
            }
            this.contentWizardStepForm = new ContentWizardStepForm();
            var pageWizardStepFormConfig: page.PageWizardStepFormConfig = {
                parentContent: this.parentContent,
                siteContent: this.siteContent
            };
            this.pageWizardStepForm = new page.PageWizardStepForm(pageWizardStepFormConfig);

            app.wizard.event.ShowContentLiveEvent.on((event) => {
                this.toggleFormPanel(false);
            });

            app.wizard.event.ShowContentFormEvent.on((event) => {
                this.toggleFormPanel(true);
            });

            if (this.contentType.hasContentDisplayNameScript()) {

                this.displayNameScriptExecutor.setScript(this.contentType.getContentDisplayNameScript());
            }

            super({
                tabId: params.tabId,
                persistedItem: params.persistedContent,
                formIcon: this.formIcon,
                mainToolbar: mainToolbar,
                stepToolbar: stepToolbar,
                header: this.contentWizardHeader,
                actions: actions,
                livePanel: this.livePanel,
                steps: this.createSteps(params.persistedContent)
            }, () => {
                console.log("ContentWizardPanel.constructor finished");
                callback(this);
            });
        }

        giveInitialFocus() {
            console.log("ContentWizardPanel.giveInitialFocus");

            var newWithoutDisplayCameScript = this.isRenderingNew() && !this.contentType.hasContentDisplayNameScript();
            var displayNameEmpty = api.util.isStringEmpty(this.getPersistedItem().getDisplayName());
            var editWithEmptyDisplayName = !this.isRenderingNew() && displayNameEmpty && !this.contentType.hasContentDisplayNameScript();

            if (newWithoutDisplayCameScript || editWithEmptyDisplayName) {
                this.contentWizardHeader.giveFocus();
            }
            else {
                if (!this.contentWizardStepForm.giveFocus()) {
                    this.contentWizardHeader.giveFocus();
                }
            }

            this.startRememberFocus();
        }

        createSteps(content: api.content.Content): api.app.wizard.WizardStep[] {

            var steps: api.app.wizard.WizardStep[] = [];

            if (this.siteWizardStepForm != null) {
                steps.push(new api.app.wizard.WizardStep("Site", this.siteWizardStepForm));
            }
            steps.push(new api.app.wizard.WizardStep(this.contentType.getDisplayName(), this.contentWizardStepForm));
            steps.push(new api.app.wizard.WizardStep("Page", this.pageWizardStepForm));
            return steps;
        }

        onElementShown() {
            if (this.getPersistedItem()) {
                app.Router.setHash("edit/" + this.getPersistedItem().getId());
            } else {
                app.Router.setHash("new/" + this.contentType.getName());
            }
            super.onElementShown();
        }

        preRenderNew(callBack: Function) {
            console.log("ContentWizardPanel.preRenderNew");

            this.persistNewItem((createdContent: api.content.Content) => {
                callBack();
            });
        }

        postRenderNew(callBack: Function) {
            console.log("ContentWizardPanel.postRenderNew");

            this.enableDisplayNameScriptExecution(this.contentWizardStepForm.getFormView());
            this.contentWizardHeader.initNames(this.getPersistedItem().getDisplayName(), this.getPersistedItem().getName().toString(),
                true);

            callBack();
        }

        postRenderExisting(callBack: Function) {
            console.log("ContentWizardPanel.postRenderExisting");

            this.contentWizardHeader.initNames(this.getPersistedItem().getDisplayName(), this.getPersistedItem().getName().toString(),
                false);
            this.enableDisplayNameScriptExecution(this.contentWizardStepForm.getFormView());
            callBack();
        }

        private enableDisplayNameScriptExecution(formView: api.form.FormView) {

            if (this.displayNameScriptExecutor.hasScript()) {

                formView.getEl().addEventListener("keyup", (e) => {
                    this.contentWizardHeader.getDisplayName();
                    this.contentWizardHeader.setDisplayName(this.displayNameScriptExecutor.execute());
                });
            }
        }

        setPersistedItem(content: api.content.Content, callback: Function) {
            console.log("ContentWizardPanel.setPersistedItem");

            super.setPersistedItem(content, () => {

                this.formIcon.setSrc(content.getIconUrl());
                var contentData: api.content.ContentData = content.getContentData();

                this.loadAttachments(content.getContentId(), (attachmentsArray: api.content.attachment.Attachment[])=> {

                    var attachments = new api.content.attachment.AttachmentsBuilder().
                        addAll(attachmentsArray).
                        build();

                    var formContext = new api.form.FormContextBuilder().
                        setParentContent(this.parentContent).
                        setPersistedContent(content).
                        setAttachments(attachments).
                        build();

                    this.contentWizardStepForm.renderExisting(formContext, contentData, content.getForm());
                    // Must pass FormView from contentWizardStepForm displayNameScriptExecutor, since a new is created for each call to renderExisting
                    this.displayNameScriptExecutor.setFormView(this.contentWizardStepForm.getFormView());

                    this.doRenderExistingSite(content, formContext)
                        .then(() => {
                            this.doRenderExistingPage(content, formContext).
                                then((pageTemplate: api.content.page.PageTemplate) => {
                                    this.doRenderLivePanel(content, pageTemplate).
                                        then(() => {
                                            callback();
                                        });
                                });
                        });
                });
            });
        }

        private doRenderExistingSite(content: api.content.Content, formContext: api.form.FormContext): Q.Promise<void> {

            var deferred = Q.defer<void>();

            if (this.siteWizardStepForm != null && content.getSite()) {
                this.siteWizardStepForm.renderExisting(formContext, content.getSite(), this.contentType, () => {
                    deferred.resolve(null);
                });
            }
            else {
                deferred.resolve(null);
            }

            return deferred.promise;
        }

        private doRenderExistingPage(content: api.content.Content, formContext: api.form.FormContext): Q.Promise<api.content.page.PageTemplate> {

            var deferred = Q.defer<api.content.page.PageTemplate>();

            this.pageWizardStepForm.renderExisting(content).
                then((pageTemplate:api.content.page.PageTemplate) => {
                    deferred.resolve(pageTemplate);
                });


            return deferred.promise;
        }

        private doRenderLivePanel(content: api.content.Content, pageTemplate: api.content.page.PageTemplate): Q.Promise<void> {

            var deferred = Q.defer<void>();

            this.livePanel.renderExisting(content, pageTemplate);

            deferred.resolve(null);
            return deferred.promise;
        }

        persistNewItem(callback: (persistedContent: api.content.Content) => void) {

            console.log("ContentWizardPanel.persistNewItem");

            var contentData = new api.content.ContentData();

            var parentPath = this.parentContent != null ? this.parentContent.getPath() : api.content.ContentPath.ROOT;

            var createRequest = new api.content.CreateContentRequest().
                setDraft(this.persistAsDraft).
                setName(api.content.ContentUnnamed.newUnnamed()).
                setParent(parentPath).
                setContentType(this.contentType.getContentTypeName()).
                setDisplayName(this.contentWizardHeader.getDisplayName()).
                setForm(this.contentType.getForm()).
                setContentData(contentData);

            createRequest.sendAndParse().
                done((createdContent: api.content.Content) => {

                    this.getTabId().changeToEditMode(createdContent.getId());

                    if (this.createSite) {

                        var moduleConfigs: api.content.site.ModuleConfig[] = [];
                        this.siteTemplate.getModules().forEach((moduleKey: api.module.ModuleKey) => {
                            var moduleConfig = new api.content.site.ModuleConfigBuilder().
                                setModuleKey(moduleKey).
                                setConfig(new api.data.RootDataSet()).
                                build();
                            moduleConfigs.push(moduleConfig);
                        });
                        new api.content.site.CreateSiteRequest(createdContent.getId())
                            .setSiteTemplateKey(this.siteTemplate.getKey())
                            .setModuleConfigs(moduleConfigs)
                            .sendAndParse().done((updatedContent: api.content.Content) => {

                                new api.content.ContentCreatedEvent(updatedContent).fire();

                                this.setPersistedItem(updatedContent, () => {

                                    callback(updatedContent);
                                });

                            });
                    }
                    else {

                        new api.content.ContentCreatedEvent(createdContent).fire();

                        this.setPersistedItem(createdContent, () => {

                            callback(createdContent);
                        });
                    }
                });
        }

        updatePersistedItem(callback: (persistedContent: api.content.Content) => void) {
            console.log("ContentWizardPanel.updatePersistedItem");

            var updateRequest = new api.content.UpdateContentRequest(this.getPersistedItem().getId()).
                setContentName(this.resolveContentNameForUpdateReuest()).
                setContentType(this.contentType.getContentTypeName()).
                setDisplayName(this.contentWizardHeader.getDisplayName()).
                setForm(this.contentWizardStepForm.getForm()).
                setContentData(this.contentWizardStepForm.getContentData());

            updateRequest.addAttachments(this.contentWizardStepForm.getFormView().getAttachments());

            if (this.iconUploadItem) {
                var attachment = new api.content.attachment.AttachmentBuilder().
                    setBlobKey(this.iconUploadItem.getBlobKey()).
                    setAttachmentName(new api.content.attachment.AttachmentName('_thumb.png')).
                    setMimeType(this.iconUploadItem.getMimeType()).
                    setSize(this.iconUploadItem.getSize()).
                    build();
                updateRequest.addAttachment(attachment);
            }

            updateRequest.
                sendAndParse().
                done((updatedContent: api.content.Content) => {

                    new api.content.ContentUpdatedEvent(updatedContent).fire();

                    if (this.siteWizardStepForm != null) {
                        new api.content.site.UpdateSiteRequest(updatedContent.getId())
                            .setSiteTemplateKey(this.siteWizardStepForm.getTemplateKey())
                            .setModuleConfigs(this.siteWizardStepForm.getModuleConfigs())
                            .sendAndParse().done((updatedSite: api.content.Content) => {

                                api.notify.showFeedback('Content was updated!');

                                this.renderExisting(updatedSite, () => {
                                    this.postRenderExisting(() => {

                                        callback(updatedSite);
                                    });
                                });
                            });
                    }
                    else {

                        api.notify.showFeedback('Content was updated!');

                        this.renderExisting(updatedContent, () => {
                            this.postRenderExisting(() => {

                                callback(updatedContent);
                            });
                        });
                    }
                });
        }

        private loadAttachments(content: api.content.ContentId, callback: { (attachments: api.content.attachment.Attachment[]) }) {
            new api.content.attachment.GetAttachmentsRequest(content).
                sendAndParse().
                done((attachments: api.content.attachment.Attachment[]) => {
                    callback(attachments);
                });
        }


        private resolveContentNameForUpdateReuest(): api.content.ContentName {
            if (api.util.isStringEmpty(this.contentWizardHeader.getName()) && this.getPersistedItem().getName().isUnnamed()) {
                return this.getPersistedItem().getName();
            }
            else {
                return api.content.ContentName.fromString(this.contentWizardHeader.getName());
            }
        }

        hasUnsavedChanges(): boolean {
            var persistedContent: api.content.Content = this.getPersistedItem();
            if (persistedContent == undefined) {
                return true;
            } else {
                return !api.util.isStringsEqual(persistedContent.getDisplayName(), this.contentWizardHeader.getDisplayName())
                           || !api.util.isStringsEqual(persistedContent.getName().toString(), this.contentWizardHeader.getName().toString())
                    || !persistedContent.getContentData().equals(this.contentWizardStepForm.getContentData());
            }
        }

        getParentContent(): api.content.Content {
            return this.parentContent;
        }

        getContentType(): api.schema.content.ContentType {
            return this.contentType;
        }
    }

}