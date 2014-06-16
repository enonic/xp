module LiveEdit.ui {

    import Body = api.dom.Body;
    import ItemView = api.liveedit.ItemView;

    export class Cursor {

        defaultBodyCursor: string;

        constructor() {
            // Cache any user set body@style cursor in order to restore it later.
            // Not 100% as the cursor can change any time during the page's life cycle.
            // wemjq.css('cursor') should be avoided here used as it uses window.getComputedStyle()
            this.defaultBodyCursor = Body.get().getEl().getCursor();
        }

        displayItemViewCursor(itemView: ItemView): void {
            if (!itemView) {
                return;
            }
            Body.get().getEl().setCursor(itemView.getType().getConfig().getCursor());
        }

        hide(): void {
            Body.get().getEl().setCursor('none');
        }

        reset(): void {
            if (LiveEdit.component.dragdropsort.DragDropSort.isDragging()) {
                return;
            }
            Body.get().getEl().setCursor(this.defaultBodyCursor || '');
        }

    }
}