import '../../../api.ts';

import i18n = api.util.i18n;
import CreatePageTemplateRequest = api.content.page.CreatePageTemplateRequest;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import EditContentEvent = api.content.event.EditContentEvent;
import Action = api.ui.Action;
import PageModel = api.content.page.PageModel;
import Permission = api.security.acl.Permission;
import ContentSummary = api.content.ContentSummary;

export class SaveAsTemplateAction
    extends Action {

    private userHasCreateRights: Boolean;

    constructor(private contentSummary?: ContentSummary, private pageModel?: PageModel) {
        super(i18n('action.saveAsTemplate'));

        this.onExecuted(action => {
            new CreatePageTemplateRequest()
                .setController(this.pageModel.getController().getKey())
                .setRegions(this.pageModel.getRegions())
                .setConfig(this.pageModel.getConfig())
                .setDisplayName(this.contentSummary.getDisplayName())
                .setSite(this.contentSummary.getPath())
                .setSupports(this.contentSummary.getType())
                .setName(this.contentSummary.getName())
                .sendAndParse().then(createdTemplate => {

                new EditContentEvent([ContentSummaryAndCompareStatus.fromContentSummary(createdTemplate)]).fire();
            });
        });
    }

    updateVisibility() {
        const hasController = this.pageModel.getController();
        if (hasController && this.contentSummary.isSite()) {
            if (this.userHasCreateRights === undefined) {
                new api.content.resource.GetPermittedActionsRequest()
                    .addContentIds(this.contentSummary.getContentId())
                    .addPermissionsToBeChecked(Permission.CREATE)
                    .sendAndParse().then((allowedPermissions: Permission[]) => {

                    this.userHasCreateRights = allowedPermissions.indexOf(Permission.CREATE) > -1;
                    this.setVisible(this.userHasCreateRights.valueOf());
                });
            } else {
                this.setVisible(this.userHasCreateRights.valueOf());
            }
        } else {
            this.setVisible(false);
        }
    }

    setContentSummary(contentSummary: ContentSummary): SaveAsTemplateAction {
        this.contentSummary = contentSummary;
        return this;
    }

    setPageModel(model: PageModel): SaveAsTemplateAction {
        this.pageModel = model;
        return this;
    }
}
