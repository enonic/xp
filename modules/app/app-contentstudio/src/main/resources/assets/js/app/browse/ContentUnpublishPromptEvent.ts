import '../../api.ts';
import {BaseContentModelEvent} from './BaseContentModelEvent';

import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;

export class ContentUnpublishPromptEvent extends BaseContentModelEvent {

    constructor(model: ContentSummaryAndCompareStatus[]) {
        super(model);
    }

    static on(handler: (event: ContentUnpublishPromptEvent) => void) {
        api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
    }

    static un(handler?: (event: ContentUnpublishPromptEvent) => void) {
        api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
    }
}
