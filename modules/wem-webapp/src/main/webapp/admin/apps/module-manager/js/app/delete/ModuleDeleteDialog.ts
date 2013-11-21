module app_delete {

    export class ModuleDeleteDialog extends api_app_delete.DeleteDialog {

        private moduleToDelete:api_module.Module;

        constructor() {
            super("Module");

            this.setDeleteAction(new ModuleDeleteDialogAction());

            this.getDeleteAction().addExecutionListener(() => {
                var deleteRequest = new api_module.DeleteModuleRequest(this.moduleToDelete.getModuleKey().toString());
                deleteRequest.send().done((resp:api_rest.JsonResponse) => {
                    var respJson = resp.getJson();
                    if (respJson.error) {
                        api_notify.showError('The Module was not deleted: ' + respJson.error.message);
                    } else {
                        api_notify.showFeedback('Module \'' + respJson.result + '\' was deleted');
                    }
                    this.close();
                }).fail(() => {
                    this.close();
                })
            });
        }

        setModuleToDelete(moduleModel:api_module.Module) {
            this.moduleToDelete = moduleModel;
            var deleteItem = new api_app_delete.DeleteItem(api_util.getAdminUri('common/images/icons/icoMoon/32x32/folder.png'), moduleModel.getDisplayName());
            var deleteItems:api_app_delete.DeleteItem[] = [deleteItem];
            this.setDeleteItems(deleteItems);
        }
    }

    export class ModuleDeleteDialogAction extends api_ui.Action {

        constructor() {
            super("Delete", "enter");
        }
    }
}