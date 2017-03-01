import '../../../api.ts';

export class PrincipalBrowseResetEvent extends api.event.Event {

    static on(handler: (event: PrincipalBrowseResetEvent) => void) {
        api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
    }

    static un(handler?: (event: PrincipalBrowseResetEvent) => void) {
        api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
    }
}
