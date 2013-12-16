module app_delete {

    export class SiteTemplateDeleteDialog extends api_app_delete.DeleteDialog {

        private siteTemplateToDelete: api_content_site_template.SiteTemplateSummary;

        constructor() {
            super("SiteTemplate");

            this.setDeleteAction(new SiteTemplateDeleteDialogAction());

            this.getDeleteAction().addExecutionListener(() => {
                var key = this.siteTemplateToDelete.getKey();
                var deleteRequest = new api_content_site_template.DeleteSiteTemplateRequest(key);
                deleteRequest.send().done((resp: api_rest.JsonResponse) => {
                    var respJson = resp.getJson();
                    api_notify.showFeedback('Site Template \'' + respJson.result + '\' was deleted');
                    new api_content_site_template.SiteTemplateDeletedEvent(api_content_site_template.SiteTemplateKey.fromString(respJson.result)).fire();
                    this.close();
                }).fail(() => {
                        this.close();
                    })
            });
        }

        setSiteTemplateToDelete(siteTemplate: api_content_site_template.SiteTemplateSummary) {
            this.siteTemplateToDelete = siteTemplate;
            var deleteItem = new api_app_delete.DeleteItem(api_util.getAdminUri('common/images/icons/icoMoon/32x32/folder.png'),
                siteTemplate.getDisplayName());
            var deleteItems: api_app_delete.DeleteItem[] = [deleteItem];
            this.setDeleteItems(deleteItems);
        }
    }

    export class SiteTemplateDeleteDialogAction extends api_ui.Action {

        constructor() {
            super("Delete", "enter");
        }
    }
}
