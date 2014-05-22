module LiveEdit.component {

    import ComponentPath = api.content.page.ComponentPath;
    import PageSelectEvent = api.liveedit.PageSelectEvent;
    import RegionSelectEvent = api.liveedit.RegionSelectEvent;
    import PageComponentSelectEvent = api.liveedit.PageComponentSelectEvent;
    import PageComponentDeselectEvent = api.liveedit.PageComponentDeselectEvent;
    import ItemView = api.liveedit.ItemView;
    import RegionView = api.liveedit.RegionView;
    import PageView = api.liveedit.PageView;
    import PageItemType = api.liveedit.PageItemType;
    import RegionItemType = api.liveedit.RegionItemType;
    import PageComponentItemType = api.liveedit.PageComponentItemType;
    import PageComponentView = api.liveedit.PageComponentView;

    // Uses
    var $ = $liveEdit;

    export var ATTRIBUTE_NAME: string = 'data-live-edit-selected';

    export class Selection {

        public static handleSelect(itemView: ItemView, event?: JQueryEventObject, waitForRender: boolean = false) {

            itemView.select();
            //this.setSelectionAttributeOnElement($(itemView));

            var mouseClickPagePosition: any = null;
            if (event && !itemView.isEmpty()) {
                mouseClickPagePosition = {
                    x: event.pageX,
                    y: event.pageY
                };
            }

            if (waitForRender) {
                var maxIterations = 10;
                var iterations = 0;
                var interval = setInterval(() => {
                    if (itemView.getHTMLElement().offsetHeight > 0) {
                        $(window).trigger('selectComponent.liveEdit', [itemView, mouseClickPagePosition]);
                        clearInterval(interval);
                    }
                    iterations++;
                    if (iterations >= maxIterations) {
                        clearInterval(interval);
                    }
                }, 300);
            } else {
                $(window).trigger('selectComponent.liveEdit', [itemView, mouseClickPagePosition]);
            }
        }

        public static pageHasSelectedElement(): boolean {
            return $('[' + ATTRIBUTE_NAME + ']').length > 0;
        }

        public static removeSelectedAttribute(): void {
            $('[' + ATTRIBUTE_NAME + ']').removeAttr(ATTRIBUTE_NAME);
        }

    }
}