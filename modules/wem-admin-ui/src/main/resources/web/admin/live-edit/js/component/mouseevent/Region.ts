module LiveEdit.component.mouseevent {

    import SortableUpdateEvent = api.liveedit.SortableUpdateEvent;

    // Uses
    var $ = $liveEdit;

    export class Region extends LiveEdit.component.mouseevent.Base {
        constructor() {
            super();

            this.componentCssSelectorFilter = LiveEdit.component.TypeConfiguration[LiveEdit.component.Type.REGION].cssSelector;

            // fixme: this does not belongs here.
            LiveEdit.PlaceholderCreator.renderEmptyRegionPlaceholders();

            this.attachMouseOverEvent();
            this.attachMouseOutEvent();
            this.attachClickEvent();
            this.registerGlobalListeners();
        }

        private registerGlobalListeners():void {

            // fixme: this does not belongs here.
            $(window).on('sortableOver.liveEdit componentRemoved.liveEdit', () => {
                LiveEdit.PlaceholderCreator.renderEmptyRegionPlaceholders();
            });

            // fixme: this does not belongs here.
            SortableUpdateEvent.on(() => {
                LiveEdit.PlaceholderCreator.renderEmptyRegionPlaceholders();
            });
        }

    }
}
