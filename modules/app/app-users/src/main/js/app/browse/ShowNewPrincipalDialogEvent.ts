import '../../api.ts';
import {UserTreeGridItem} from './UserTreeGridItem';

export class ShowNewPrincipalDialogEvent extends api.event.Event {

    private selection: UserTreeGridItem[];

    constructor(selection: UserTreeGridItem[]) {
        super();
        this.selection = selection;
    }

    getSelection(): UserTreeGridItem[] {
        return this.selection;
    }

    static on(handler: (event: ShowNewPrincipalDialogEvent) => void) {
        api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
    }

    static un(handler?: (event: ShowNewPrincipalDialogEvent) => void) {
        api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
    }
}
