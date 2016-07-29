import "../../api.ts";

export class ContentWizardPanelParams {

    createSite: boolean = false;

    tabId: api.app.bar.AppBarTabId;

    contentTypeName: api.schema.content.ContentTypeName;

    parentContent: api.content.Content;

    contentSummary: api.content.ContentSummary;


    setTabId(value: api.app.bar.AppBarTabId): ContentWizardPanelParams {
        this.tabId = value;
        return this;
    }

    setContentTypeName(value: api.schema.content.ContentTypeName): ContentWizardPanelParams {
        this.contentTypeName = value;
        return this;
    }

    setParentContent(value: api.content.Content): ContentWizardPanelParams {
        this.parentContent = value;
        return this;
    }

    setContentSummary(value: api.content.ContentSummary): ContentWizardPanelParams {
        this.contentSummary = value;
        return this;
    }

    setCreateSite(value: boolean): ContentWizardPanelParams {
        this.createSite = value;
        return this;
    }
}
