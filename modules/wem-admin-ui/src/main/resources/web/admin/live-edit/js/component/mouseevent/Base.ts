module LiveEdit.component.mouseevent {

    import ItemView = api.liveedit.ItemView;

    export class Base {
        public componentCssSelectorFilter: string = '';

        constructor() {
        }

        attachMouseOverEvent(): void {

            wemjq(document).on('mouseover', this.componentCssSelectorFilter, (event: JQueryEventObject) => {
                if (this.cancelMouseOverEvent(event)) {
                    return;
                }
                event.stopPropagation();

                LiveEdit.LiveEditPage.get().deselectSelectedView();
                var itemView = LiveEdit.LiveEditPage.get().getItemViewByHTMLElement(<HTMLElement>event.currentTarget);
                if (itemView) {
                    wemjq(window).trigger('mouseOverComponent.liveEdit', [ itemView ]);
                }
            });
        }

        attachMouseOutEvent(): void {
            wemjq(document).on('mouseout', this.componentCssSelectorFilter, (event: JQueryEventObject) => {
                if (LiveEdit.LiveEditPage.get().hasSelectedView()) {
                    return;
                }

                LiveEdit.LiveEditPage.get().deselectSelectedView();
                var itemView = LiveEdit.LiveEditPage.get().getItemViewByHTMLElement(<HTMLElement>event.currentTarget);
                wemjq(window).trigger('mouseOutComponent.liveEdit', [itemView]);
            });
        }

        attachClickEvent(): void {

            wemjq(document).on('click contextmenu touchstart', this.componentCssSelectorFilter, (event: JQueryEventObject) => {
                if (this.targetIsLiveEditUiComponent(wemjq(event.target))) {
                    return;
                }

                // Make sure the event is not propagated to any parent
                event.stopPropagation();

                // Needed so the browser's context menu is not shown on contextmenu
                event.preventDefault();

                var itemView = LiveEdit.LiveEditPage.get().getItemViewByHTMLElement(<HTMLElement>event.currentTarget);
                itemView.select((event && !itemView.isEmpty()) ? { x: event.pageX, y: event.pageY } : null);
            });
        }

        // fixme: move when empty placeholder stuff is refactored
        getAll(): JQuery {
            return wemjq(this.componentCssSelectorFilter);
        }

        cancelMouseOverEvent(event: JQueryEventObject): boolean {
            return this.targetIsLiveEditUiComponent(wemjq(event.target)) || LiveEdit.LiveEditPage.get().hasSelectedView() ||
                   LiveEdit.component.dragdropsort.DragDropSort.isDragging();
        }

        private targetIsLiveEditUiComponent(target: JQuery): boolean {
            var uiComponentSelector = '.' + LiveEdit.ui.Base.LIVE_EDIT_UI_COMPONENT;
            return target.is(uiComponentSelector) || target.closest(uiComponentSelector).length > 0;
        }

    }
}
