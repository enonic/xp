module app.wizard {

    export class ContentWizardPanelParams {

        createSite: boolean = false;

        tabId: api.app.bar.AppBarTabId;

        contentType: api.schema.content.ContentType;

        parentContent: api.content.Content;

        persistedContent: api.content.Content;

        site: api.content.site.Site;

        defaultModels: app.wizard.page.DefaultModels;

        setAppBarTabId(value: api.app.bar.AppBarTabId): ContentWizardPanelParams {
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

        setSite(value: api.content.site.Site): ContentWizardPanelParams {
            this.site = value;
            return this;
        }

        setDefaultModels(value: page.DefaultModels): ContentWizardPanelParams {
            this.defaultModels = value;
            return this;
        }

        setCreateSite(value: boolean): ContentWizardPanelParams {
            this.createSite = value;
            return this;
        }
    }
}