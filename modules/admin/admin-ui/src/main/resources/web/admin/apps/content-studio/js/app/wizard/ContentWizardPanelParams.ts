import "../../api.ts";
import AppBarTabId = api.app.bar.AppBarTabId;
import ContentTypeName = api.schema.content.ContentTypeName;
import ContentId = api.content.ContentId;
import Content = api.content.Content;
import ContentBuilder = api.content.ContentBuilder;
import Path = api.rest.Path;
import Application = api.app.Application;

export class ContentWizardPanelParams {

    application: Application;

    createSite: boolean = false;

    tabId: api.app.bar.AppBarTabId;

    contentTypeName: api.schema.content.ContentTypeName;

    parentContentId: api.content.ContentId;

    contentId: api.content.ContentId;

    setApplication(app: Application): ContentWizardPanelParams {
        this.application = app;
        return this;
    }

    setTabId(value: api.app.bar.AppBarTabId): ContentWizardPanelParams {
        this.tabId = value;
        return this;
    }

    setContentTypeName(value: api.schema.content.ContentTypeName): ContentWizardPanelParams {
        this.contentTypeName = value;
        return this;
    }

    setParentContentId(value: api.content.ContentId): ContentWizardPanelParams {
        this.parentContentId = value;
        return this;
    }

    setContentId(value: api.content.ContentId): ContentWizardPanelParams {
        this.contentId = value;
        return this;
    }

    setCreateSite(value: boolean): ContentWizardPanelParams {
        this.createSite = value;
        return this;
    }

    toString(): string {
        return this.contentId ?
               'edit/' + this.contentId.toString() :
               'new/' + this.contentTypeName.toString() +
               (this.parentContentId ? '/' + this.parentContentId.toString() : '');
    }

    static fromApp(app: Application): ContentWizardPanelParams {
        let path = app.getPath(),
            tabId, wizardParams;
        switch (path.getElement(0)) {
        case 'new':
            let contentTypeName = new ContentTypeName(path.getElement(1));
            let parentContentId;
            if (path.getElement(2)) {
                parentContentId = new ContentId(path.getElement(2));
            }
            tabId = AppBarTabId.forNew(contentTypeName.getApplicationKey().getName());
            wizardParams = new ContentWizardPanelParams()
                .setApplication(app)
                .setContentTypeName(contentTypeName)
                .setParentContentId(parentContentId)
                .setCreateSite(contentTypeName.isSite())
                .setTabId(tabId);
            break;
        case 'edit':
            let contentId = new ContentId(path.getElement(1));
            tabId = AppBarTabId.forEdit(contentId.toString());
            wizardParams = new ContentWizardPanelParams()
                .setApplication(app)
                .setContentId(contentId)
                .setTabId(tabId);
            break;
        }
        return wizardParams;
    }
}
