import "../../api.ts";

export class ContentDeleteDialogAction extends api.ui.Action {
    constructor() {
        super("Delete");
        this.setIconClass("delete-action");
    }
}
