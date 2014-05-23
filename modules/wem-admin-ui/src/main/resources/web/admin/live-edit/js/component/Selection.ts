module LiveEdit.component {

    import ItemView = api.liveedit.ItemView;
    import PageComponentSelectComponentEvent = api.liveedit.PageComponentSelectComponentEvent;

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
                        new PageComponentSelectComponentEvent(itemView, mouseClickPagePosition).fire();
                        clearInterval(interval);
                    }
                    iterations++;
                    if (iterations >= maxIterations) {
                        clearInterval(interval);
                    }
                }, 300);
            } else {
                new PageComponentSelectComponentEvent(itemView, mouseClickPagePosition).fire();
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