import '../../../api.ts';

import i18n = api.util.i18n;
import CreatePageTemplateRequest = api.content.page.CreatePageTemplateRequest;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import EditContentEvent = api.content.event.EditContentEvent;
import Action = api.ui.Action;
import PageModel = api.content.page.PageModel;
import Permission = api.security.acl.Permission;

export class SaveAsTemplateAction
    extends Action {

    private userHasCreateRights: Boolean;

    constructor(private content?: Content, private pageModel?: PageModel) {
        super(i18n('action.saveAsTemplate'));

        this.onExecuted(action => {
            new CreatePageTemplateRequest()
                .setController(this.pageModel.getController().getKey())
                .setRegions(this.pageModel.getRegions())
                .setConfig(this.pageModel.getConfig())
                .setDisplayName(this.content.getDisplayName())
                .setSite(this.content.getPath())
                .setSupports(this.content.getType())
                .setName(this.content.getName())
                .sendAndParse().then(createdTemplate => {

                new EditContentEvent([ContentSummaryAndCompareStatus.fromContentSummary(createdTemplate)]).fire();
            });
        });
    }

    updateVisibility() {
        const hasController = this.pageModel.getController();
        if (hasController && this.content.isSite()) {
            if (this.userHasCreateRights === undefined) {
                new api.content.resource.GetPermittedActionsRequest()
                    .addContentIds(this.content.getContentId())
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

    setContent(content: Content): SaveAsTemplateAction {
        this.content = content;
        return this;
    }

    setPageModel(model: PageModel): SaveAsTemplateAction {
        this.pageModel = model;
        return this;
    }
}
