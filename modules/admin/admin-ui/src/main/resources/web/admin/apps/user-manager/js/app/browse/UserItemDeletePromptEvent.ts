import '../../api.ts';
import {BaseUserEvent} from './BaseUserEvent';

export class UserItemDeletePromptEvent extends BaseUserEvent {

    static on(handler: (event: UserItemDeletePromptEvent) => void) {
        api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
    }

    static un(handler?: (event: UserItemDeletePromptEvent) => void) {
        api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
    }
}
