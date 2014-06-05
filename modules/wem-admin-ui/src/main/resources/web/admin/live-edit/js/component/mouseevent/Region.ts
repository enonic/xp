module LiveEdit.component.mouseevent {

    import RegionItemType = api.liveedit.RegionItemType;
    import PageComponentRemoveEvent = api.liveedit.PageComponentRemoveEvent;
    import SortableUpdateEvent = api.liveedit.SortableUpdateEvent;

    export class Region extends LiveEdit.component.mouseevent.Base {
        constructor() {
            super();

            this.componentCssSelectorFilter = RegionItemType.get().getConfig().getCssSelector();

            // fixme: this does not belongs here.
            //LiveEdit.PlaceholderCreator.renderEmptyRegionPlaceholders();

            this.attachMouseOverEvent();
            this.attachMouseOutEvent();
            this.attachClickEvent();
            this.registerGlobalListeners();
        }

        private registerGlobalListeners(): void {

            //wemjq(window).on('sortableOver.liveEdit', () => {
            //LiveEdit.PlaceholderCreator.renderEmptyRegionPlaceholders();
            //});

            //PageComponentRemoveEvent.on(() => LiveEdit.PlaceholderCreator.renderEmptyRegionPlaceholders());

            //SortableUpdateEvent.on(() => LiveEdit.PlaceholderCreator.renderEmptyRegionPlaceholders());
        }

    }
}
