import '../../api.ts';

export class ShowIssuesDialogEvent extends api.event.Event {

    static on(handler: (event: ShowIssuesDialogEvent) => void) {
        api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
    }

    static un(handler?: (event: ShowIssuesDialogEvent) => void) {
        api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
    }
}
