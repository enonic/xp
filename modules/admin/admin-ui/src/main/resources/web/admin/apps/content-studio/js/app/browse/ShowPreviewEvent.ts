import '../../api.ts';
import {BaseContentModelEvent} from './BaseContentModelEvent';

export class ShowPreviewEvent extends BaseContentModelEvent {

    static on(handler: (event: ShowPreviewEvent) => void) {
        api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
    }

    static un(handler?: (event: ShowPreviewEvent) => void) {
        api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
    }
}
