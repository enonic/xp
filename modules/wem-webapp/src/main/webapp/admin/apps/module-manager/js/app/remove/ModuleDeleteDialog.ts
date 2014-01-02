module app.remove {

    export class ModuleDeleteDialog extends api.app.remove.DeleteDialog {

        private moduleToDelete:api.module.ModuleSummary;

        constructor() {
            super("Module");

            this.setDeleteAction(new ModuleDeleteDialogAction());

            this.getDeleteAction().addExecutionListener(() => {
                var deleteRequest = new api.module.DeleteModuleRequest(this.moduleToDelete.getModuleKey().toString());
                deleteRequest.send().done((resp:api.rest.JsonResponse<any>) => {
                    var respJson = resp.getJson();
                    if (respJson.error) {
                        api.notify.showError('The Module was not deleted: ' + respJson.error.message);
                    } else {
                        api.notify.showFeedback('Module \'' + respJson + '\' was deleted');
                        new api.module.ModuleDeletedEvent(api.module.ModuleKey.fromString(respJson)).fire();
                    }
                    this.close();
                }).fail(() => {
                    this.close();
                })
            });
        }

        setModuleToDelete(moduleModel:api.module.ModuleSummary) {
            this.moduleToDelete = moduleModel;
            var deleteItem = new api.app.remove.DeleteItem(api.util.getAdminUri('common/images/icons/icoMoon/32x32/folder.png'), moduleModel.getDisplayName());
            var deleteItems:api.app.remove.DeleteItem[] = [deleteItem];
            this.setDeleteItems(deleteItems);
        }
    }

    export class ModuleDeleteDialogAction extends api.ui.Action {

        constructor() {
            super("Delete", "enter");
        }
    }
}
