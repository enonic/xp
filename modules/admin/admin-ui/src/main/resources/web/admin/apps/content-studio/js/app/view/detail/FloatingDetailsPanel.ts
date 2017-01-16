import "../../../api.ts";
import {SlidablePanel, SlidablePanelBuilder} from "./SlidablePanel";
import {DetailsView} from "./DetailsView";
import {DETAILS_PANEL_TYPE} from "./DetailsPanel";

import ResponsiveManager = api.ui.responsive.ResponsiveManager;

export class FloatingDetailsPanel extends SlidablePanel {

    private splitter: api.dom.DivEl;
    private ghostDragger: api.dom.DivEl;
    private mask: api.ui.mask.DragMask;

    private minWidth: number = 280;
    private parentMinWidth: number = 15;
    private actualWidth: number;

    constructor(detailsView: DetailsView) {
        super(new SlidablePanelBuilder(), detailsView);
        this.addClass('floating-details-panel');

        this.splitter = new api.dom.DivEl('splitter');
        this.ghostDragger = new api.dom.DivEl('ghost-dragger');

        this.appendChild(this.splitter);
        this.onRendered(() => this.onRenderedHandler());
    }

    private onRenderedHandler() {
        let initialPos = 0;
        let splitterPosition = 0;
        this.actualWidth = this.getEl().getWidth();
        this.mask = new api.ui.mask.DragMask(this.getParentElement());

        let dragListener = (e: MouseEvent) => {
            if (this.splitterWithinBoundaries(initialPos - e.clientX)) {
                splitterPosition = e.clientX;
                this.ghostDragger.getEl().setLeftPx(e.clientX - this.getEl().getOffsetLeft());
            }
        };

        this.splitter.onMouseDown((e: MouseEvent) => {
            e.preventDefault();
            initialPos = e.clientX;
            splitterPosition = e.clientX;
            this.ghostDragger.insertBeforeEl(this.splitter);
            this.startDrag(dragListener);
        });

        this.mask.onMouseUp((e: MouseEvent) => {
            if (this.ghostDragger.getHTMLElement().parentNode) {
                this.actualWidth = this.getEl().getWidth() + initialPos - splitterPosition;
                this.stopDrag(dragListener);
                this.removeChild(this.ghostDragger);
                ResponsiveManager.fireResizeEvent();
            }
        });
    }

    public resetWidgetsWidth() {
        this.detailsView.resetWidgetsWidth();
    }

    public setWidthPx(value: number) {
        this.getEl().setWidthPx(value);
        this.actualWidth = value;
    }

    public getActualWidth(): number {
        return this.actualWidth;
    }

    private splitterWithinBoundaries(offset: number) {
        let newWidth = this.actualWidth + offset;
        return (newWidth >= this.minWidth) && (newWidth <= this.getParentElement().getEl().getWidth() - this.parentMinWidth);
    }

    private startDrag(dragListener: {(e: MouseEvent): void}) {
        this.mask.show();
        this.addClass('dragging');
        this.mask.onMouseMove(dragListener);
        this.ghostDragger.getEl().setLeftPx(this.splitter.getEl().getOffsetLeftRelativeToParent()).setTop(null);
    }

    private stopDrag(dragListener: {(e: MouseEvent): void}) {
        this.getEl().setWidthPx(this.actualWidth);
        this.removeClass('dragging');

        setTimeout(() => {
            this.notifyPanelSizeChanged();
            this.detailsView.getWidgetsSelectionRow().render();
        }, 800); //delay is required due to animation time

        this.mask.hide();
        this.mask.unMouseMove(dragListener);
    }

    public getType(): DETAILS_PANEL_TYPE {
        return DETAILS_PANEL_TYPE.FLOATING;
    }
}
