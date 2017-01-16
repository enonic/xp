import "../../api.ts";
import {UserTreeGridActions} from "./UserTreeGridActions";

export class UserBrowseToolbar extends api.ui.toolbar.Toolbar {

    constructor(actions: UserTreeGridActions) {
        super();
        this.addClass('user-browse-toolbar');
        this.addActions(actions.getAllActions());
    }
}
