import '../../api.ts';
import ContentSummary = api.content.ContentSummary;

export class ToggleSearchPanelWithDependenciesEvent extends api.event.Event {

    private item: ContentSummary;

    private inbound: boolean;

    constructor(item: ContentSummary, inbound: boolean) {
        super();
        this.item = item;
        this.inbound = inbound;
    }

    getContent(): ContentSummary {
        return this.item;
    }

    isInbound(): boolean {
        return this.inbound;
    }

    static on(handler: (event: ToggleSearchPanelWithDependenciesEvent) => void, contextWindow: Window = window) {
        api.event.Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
    }

    static un(handler?: (event: ToggleSearchPanelWithDependenciesEvent) => void, contextWindow: Window = window) {
        api.event.Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
    }
}
