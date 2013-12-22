module app_wizard {

    export class ContentWizardPanelParams {

        createSite: boolean = false;

        tabId: api_app.AppBarTabId;

        contentType: api_schema_content.ContentType;

        parentContent: api_content.Content;

        persistedContent: api_content.Content;

        site: api_content.Content;

        siteTemplate: api_content_site_template.SiteTemplate;

        setAppBarTabId(value: api_app.AppBarTabId): ContentWizardPanelParams {
            this.tabId = value;
            return this;
        }

        setContentType(value: api_schema_content.ContentType): ContentWizardPanelParams {
            this.contentType = value;
            return this;
        }

        setParentContent(value: api_content.Content): ContentWizardPanelParams {
            this.parentContent = value;
            return this;
        }

        setPersistedContent(value: api_content.Content): ContentWizardPanelParams {
            this.persistedContent = value;
            return this;
        }

        setSite(value: api_content.Content): ContentWizardPanelParams {
            this.site = value;
            return this;
        }

        setCreateSite(value: api_content_site_template.SiteTemplate): ContentWizardPanelParams {
            this.siteTemplate = value;
            this.createSite = this.siteTemplate != null;
            return this;
        }

    }

    export class ContentWizardPanel extends api_app_wizard.WizardPanel<api_content.Content> {

        private parentContent: api_content.Content;

        private siteContent: api_content.Content;

        private contentType: api_schema_content.ContentType;

        private formIcon: api_app_wizard.FormIcon;

        private contentWizardHeader: api_app_wizard.WizardHeaderWithDisplayNameAndName;

        private siteWizardStepForm: app_wizard_site.SiteWizardStepForm;

        private contentWizardStepForm: ContentWizardStepForm;

        private pageWizardStepForm: PageWizardStepForm;

        private iconUploadItem: api_ui.UploadItem;

        private displayNameScriptExecutor: DisplayNameScriptExecutor;

        private livePanel: LiveFormPanel;

        private persistAsDraft: boolean;

        private createSite: boolean;

        private siteTemplate: api_content_site_template.SiteTemplate;

        constructor(params: ContentWizardPanelParams, callback: (wizard: ContentWizardPanel) => void) {

            console.log("ContentWizardPanel.constructor started");

            this.persistAsDraft = true;
            this.parentContent = params.parentContent;
            this.siteContent = params.site;
            this.contentType = params.contentType;
            this.displayNameScriptExecutor = new DisplayNameScriptExecutor();
            this.contentWizardHeader = new api_app_wizard.WizardHeaderWithDisplayNameAndNameBuilder().
                setDisplayNameGenerator(this.displayNameScriptExecutor).
                build();

            var iconUrl = api_content.ContentIconUrlResolver.default();
            this.formIcon = new api_app_wizard.FormIcon(iconUrl, "Click to upload icon",
                api_util.getRestUri("blob/upload"));

            this.formIcon.addListener({

                onUploadFinished: (uploadItem: api_ui.UploadItem) => {

                    this.iconUploadItem = uploadItem;
                    this.formIcon.setSrc(api_util.getRestUri('blob/' + this.iconUploadItem.getBlobKey()));
                }
            });

            var actions = new app_wizard_action.ContentWizardActions(this);

            var mainToolbar = new ContentWizardToolbar({
                saveAction: actions.getSaveAction(),
                duplicateAction: actions.getDuplicateAction(),
                deleteAction: actions.getDeleteAction(),
                closeAction: actions.getCloseAction(),
                publishAction: actions.getPublishAction()
            });

            var stepToolbar = new api_ui_toolbar.Toolbar();

            this.livePanel = new LiveFormPanel(this.siteContent);

            if (this.parentContent) {
                this.contentWizardHeader.setPath(this.parentContent.getPath().toString() + "/");
            } else {
                this.contentWizardHeader.setPath("/");
            }

            this.createSite = params.createSite;
            this.siteTemplate = params.siteTemplate;
            if (this.createSite || params.persistedContent != null && params.persistedContent.isSite()) {
                this.siteWizardStepForm = new app_wizard_site.SiteWizardStepForm();

            }
            else {
                this.siteWizardStepForm = null;
            }
            this.contentWizardStepForm = new ContentWizardStepForm();
            var pageWizardStepFormConfig: PageWizardStepFormConfig = {
                parentContent: this.parentContent,
                siteContent: this.siteContent
            };
            this.pageWizardStepForm = new PageWizardStepForm(pageWizardStepFormConfig);

            app_wizard_event.ShowContentLiveEvent.on((event) => {
                this.toggleFormPanel(false);
            });

            app_wizard_event.ShowContentFormEvent.on((event) => {
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
            var displayNameEmpty = api_util.isStringEmpty(this.getPersistedItem().getDisplayName());
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

        createSteps(content: api_content.Content): api_app_wizard.WizardStep[] {

            var steps: api_app_wizard.WizardStep[] = [];

            if (this.siteWizardStepForm != null) {
                steps.push(new api_app_wizard.WizardStep("Site", this.siteWizardStepForm));
            }
            steps.push(new api_app_wizard.WizardStep(this.contentType.getDisplayName(), this.contentWizardStepForm));
            steps.push(new api_app_wizard.WizardStep("Page", this.pageWizardStepForm));
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

            this.persistNewItem((createdContent: api_content.Content) => {
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

        private enableDisplayNameScriptExecution(formView: api_form.FormView) {

            if (this.displayNameScriptExecutor.hasScript()) {

                formView.getEl().addEventListener("keyup", (e) => {
                    this.contentWizardHeader.getDisplayName();
                    this.contentWizardHeader.setDisplayName(this.displayNameScriptExecutor.execute());
                });
            }
        }

        setPersistedItem(content: api_content.Content, callback: Function) {
            console.log("ContentWizardPanel.setPersistedItem");

            super.setPersistedItem(content, () => {

                this.formIcon.setSrc(content.getIconUrl());
                var contentData: api_content.ContentData = content.getContentData();

                var formContext = new api_form.FormContextBuilder().
                    setParentContent(this.parentContent).
                    setPersistedContent(content).
                    build();

                this.contentWizardStepForm.renderExisting(formContext, contentData, content.getForm());
                // Must pass FormView from contentWizardStepForm displayNameScriptExecutor, since a new is created for each call to renderExisting
                this.displayNameScriptExecutor.setFormView(this.contentWizardStepForm.getFormView());

                if (content.isPage()) {
                    var page = content.getPage();

                    new api_content_page.GetPageTemplateByKeyRequest(page.getTemplate()).
                        sendAndParse().
                        done((pageTemplate: api_content_page.PageTemplate) => {

                            this.pageWizardStepForm.renderExisting(content, pageTemplate);
                            this.livePanel.renderExisting(content, pageTemplate);

                            if (this.siteWizardStepForm != null && content.getSite()) {
                                this.siteWizardStepForm.renderExisting(formContext, content.getSite(), this.contentType, () => {
                                    callback();
                                });
                            }
                            else {
                                callback();
                            }
                        });
                }
                else {

                    if (this.siteWizardStepForm != null && content.isSite()) {
                        this.siteWizardStepForm.renderExisting(formContext, content.getSite(), this.contentType, () => {
                            callback();
                        });
                    }
                    else {
                        callback();
                    }
                }
            });
        }

        persistNewItem(callback: (persistedContent: api_content.Content) => void) {

            console.log("ContentWizardPanel.persistNewItem");

            var contentData = new api_content.ContentData();

            var parentPath = this.parentContent != null ? this.parentContent.getPath() : api_content.ContentPath.ROOT;

            var createRequest = new api_content.CreateContentRequest().
                setDraft(this.persistAsDraft).
                setName(api_content.ContentUnnamed.newUnnamed()).
                setParent(parentPath).
                setContentType(this.contentType.getContentTypeName()).
                setDisplayName(this.contentWizardHeader.getDisplayName()).
                setForm(this.contentType.getForm()).
                setContentData(contentData);

            createRequest.sendAndParse().
                done((createdContent: api_content.Content) => {

                    this.getTabId().changeToEditMode(createdContent.getId());

                    if (this.createSite) {

                        var moduleConfigs: api_content_site.ModuleConfig[] = [];
                        this.siteTemplate.getModules().forEach((moduleKey: api_module.ModuleKey) => {
                            var moduleConfig = new api_content_site.ModuleConfigBuilder().
                                setModuleKey(moduleKey).
                                setConfig(new api_data.RootDataSet()).
                                build();
                            moduleConfigs.push(moduleConfig);
                        });
                        new api_content_site.CreateSiteRequest(createdContent.getId())
                            .setSiteTemplateKey(this.siteTemplate.getKey())
                            .setModuleConfigs(moduleConfigs)
                            .sendAndParse().done((updatedContent: api_content.Content) => {

                                new api_content.ContentCreatedEvent(updatedContent).fire();

                                this.setPersistedItem(updatedContent, () => {

                                    callback(updatedContent);
                                });

                            });
                    }
                    else {

                        new api_content.ContentCreatedEvent(createdContent).fire();

                        this.setPersistedItem(createdContent, () => {

                            callback(createdContent);
                        });
                    }
                });
        }

        updatePersistedItem(callback: (persistedContent: api_content.Content) => void) {
            console.log("ContentWizardPanel.updatePersistedItem");

            var updateRequest = new api_content.UpdateContentRequest(this.getPersistedItem().getId()).
                setContentName(this.resolveContentNameForUpdateReuest()).
                setContentType(this.contentType.getContentTypeName()).
                setDisplayName(this.contentWizardHeader.getDisplayName()).
                setForm(this.contentWizardStepForm.getForm()).
                setContentData(this.contentWizardStepForm.getContentData());

            updateRequest.addAttachments(this.contentWizardStepForm.getFormView().getAttachments());

            if (this.iconUploadItem) {
                var attachment = new api_content.AttachmentBuilder().
                    setBlobKey(this.iconUploadItem.getBlobKey()).
                    setAttachmentName(new api_content.AttachmentName('_thumb.png')).
                    setMimeType(this.iconUploadItem.getMimeType()).
                    setSize(this.iconUploadItem.getSize()).
                    build();
                updateRequest.addAttachment(attachment);
            }

            updateRequest.
                sendAndParse().
                done((updatedContent: api_content.Content) => {

                    new api_content.ContentUpdatedEvent(updatedContent).fire();

                    if (this.siteWizardStepForm != null) {
                        new api_content_site.UpdateSiteRequest(updatedContent.getId())
                            .setSiteTemplateKey(this.siteWizardStepForm.getTemplateKey())
                            .setModuleConfigs(this.siteWizardStepForm.getModuleConfigs())
                            .sendAndParse().done((updatedSite: api_content.Content) => {

                                api_notify.showFeedback('Content was updated!');

                                this.renderExisting(updatedSite, () => {
                                    this.postRenderExisting(() => {

                                        callback(updatedSite);
                                    });
                                });
                            });
                    }
                    else {

                        api_notify.showFeedback('Content was updated!');

                        this.renderExisting(updatedContent, () => {
                            this.postRenderExisting(() => {

                                callback(updatedContent);
                            });
                        });
                    }
                });
        }

        private resolveContentNameForUpdateReuest(): api_content.ContentName {
            if (api_util.isStringEmpty(this.contentWizardHeader.getName()) && this.getPersistedItem().getName().isUnnamed()) {
                return this.getPersistedItem().getName();
            }
            else {
                return api_content.ContentName.fromString(this.contentWizardHeader.getName());
            }
        }

        hasUnsavedChanges(): boolean {
            var persistedContent: api_content.Content = this.getPersistedItem();
            if (persistedContent == undefined) {
                return true;
            } else {
                return !this.stringsEqual(persistedContent.getDisplayName(), this.contentWizardHeader.getDisplayName())
                           || !this.stringsEqual(persistedContent.getName().toString(), this.contentWizardHeader.getName().toString())
                    || !persistedContent.getContentData().equals(this.contentWizardStepForm.getContentData());
            }
        }

        getParentContent(): api_content.Content {
            return this.parentContent;
        }

        getContentType(): api_schema_content.ContentType {
            return this.contentType;
        }

        private stringsEqual(str1: string, str2: string): boolean {
            // strings are equal if both of them are empty or not specified or they are identical
            return (!str1 && !str2) || (str1 == str2);
        }
    }

}