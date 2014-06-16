module app.remove {

    export class SiteTemplateDeleteDialog extends api.app.remove.DeleteDialog {

        private siteTemplateToDelete: app.browse.TemplateSummary;

        constructor() {
            super("SiteTemplate");

            this.setDeleteAction(new SiteTemplateDeleteDialogAction());

            this.getDeleteAction().onExecuted(() => {
                var key = this.siteTemplateToDelete.getSiteTemplateKey();
                var deleteRequest = new api.content.site.template.DeleteSiteTemplateRequest(key);
                deleteRequest.send().then((resp: api.rest.JsonResponse<any>) => {
                    var respJson = resp.getJson();
                    api.notify.showFeedback('Site Template \'' + respJson.result + '\' was deleted');
                    new api.content.site.template.SiteTemplateDeletedEvent(api.content.site.template.SiteTemplateKey.fromString(respJson.result)).fire();
                    this.close();
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).finally(() => {
                    this.close();
                }).done();
            });
        }

        setSiteTemplateToDelete(siteTemplate: app.browse.TemplateSummary) {
            this.siteTemplateToDelete = siteTemplate;
            var deleteItem = new api.app.remove.DeleteItem(api.util.getAdminUri('common/images/icons/icoMoon/32x32/earth.png'),
                siteTemplate.getDisplayName());
            var deleteItems: api.app.remove.DeleteItem[] = [deleteItem];
            this.setDeleteItems(deleteItems);
        }
    }

    export class SiteTemplateDeleteDialogAction extends api.ui.Action {

        constructor() {
            super("Delete", "enter");
        }
    }
}
