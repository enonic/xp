import "../../api.ts";
import {BaseContentModelEvent} from "./BaseContentModelEvent";

import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;

export class ContentPublishPromptEvent extends BaseContentModelEvent {

    private includeChildItems: boolean;

    constructor(model: ContentSummaryAndCompareStatus[], includeChildItems: boolean = false) {
        super(model);
        this.includeChildItems = includeChildItems;
    }

    isIncludeChildItems(): boolean {
        return this.includeChildItems;
    }

    static on(handler: (event: ContentPublishPromptEvent) => void) {
        api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
    }

    static un(handler?: (event: ContentPublishPromptEvent) => void) {
        api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
    }
}
