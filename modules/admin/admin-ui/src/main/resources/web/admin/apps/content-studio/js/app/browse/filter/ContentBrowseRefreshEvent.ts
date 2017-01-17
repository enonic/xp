import '../../../api.ts';

export class ContentBrowseRefreshEvent extends api.event.Event {

    static on(handler: (event: ContentBrowseRefreshEvent) => void) {
        api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
    }

    static un(handler?: (event: ContentBrowseRefreshEvent) => void) {
        api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
    }
}
