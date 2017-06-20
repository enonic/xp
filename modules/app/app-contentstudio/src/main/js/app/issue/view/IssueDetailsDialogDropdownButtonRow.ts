import DropdownButtonRow = api.ui.dialog.DropdownButtonRow;
import MenuButton = api.ui.button.MenuButton;
import Action = api.ui.Action;

export class IssueDetailsDialogButtonRow extends DropdownButtonRow {

    makeActionMenu(mainAction: Action, menuActions: Action[], useDefault: boolean = true): MenuButton {
        super.makeActionMenu(mainAction, menuActions, useDefault);

        return <MenuButton>this.actionMenu.addClass('issue-dialog-menu');
    }

}
