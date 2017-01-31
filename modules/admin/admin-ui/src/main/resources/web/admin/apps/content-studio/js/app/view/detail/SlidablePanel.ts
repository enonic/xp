import '../../../api.ts';
import {DetailsPanel} from './DetailsPanel';
import {DetailsView} from './DetailsView';

import ContentVersionSetEvent = api.content.event.ActiveContentVersionSetEvent;

export class SlidablePanel extends DetailsPanel {

    private slideInFunction: () => void;
    private slideOutFunction: () => void;

    private slidedInListeners: {(): void}[] = [];
    private slidedIn: boolean;

    constructor(builder: SlidablePanelBuilder, detailsView: DetailsView) {
        super(detailsView);

        this.setDoOffset(false);
        this.initSlideFunctions(builder.getSlideFrom());
        this.onSlidedIn(() => !!this.getItem() ? this.detailsView.updateActiveWidget() : null);
    }

    public isVisibleOrAboutToBeVisible(): boolean {
        return this.isSlidedIn();
    }

    slideIn() {
        this.slideInFunction();
        this.slidedIn = true;
        this.notifySlidedIn();
    }

    slideOut() {
        this.slideOutFunction();
        this.slidedIn = false;
    }

    public isSlidedIn(): boolean {
        return this.slidedIn;
    }

    private initSlideFunctions(slideFrom: SLIDE_FROM) {
        switch (slideFrom) {
        case SLIDE_FROM.RIGHT:
            this.slideInFunction = this.slideInRight;
            this.slideOutFunction = this.slideOutRight;
            break;
        case SLIDE_FROM.LEFT:
            this.slideInFunction = this.slideInLeft;
            this.slideOutFunction = this.slideOutLeft;
            break;
        case SLIDE_FROM.TOP:
            this.slideInFunction = this.slideInTop;
            this.slideOutFunction = this.slideOutTop;
            break;
        case SLIDE_FROM.BOTTOM:
            this.slideInFunction = this.slideInBottom;
            this.slideOutFunction = this.slideOutBottom;
            break;
        default:
            this.slideInFunction = this.slideInRight;
            this.slideOutFunction = this.slideOutRight;
        }
    }

    protected slideInRight() {
        this.getEl().setRightPx(0);
    }

    protected slideOutRight() {
        this.getEl().setRightPx(-this.getEl().getWidthWithBorder());
    }

    protected slideInLeft() {
        this.getEl().setLeftPx(0);
    }

    protected slideOutLeft() {
        this.getEl().setLeftPx(-this.getEl().getWidthWithBorder());
    }

    protected slideInTop() {
        this.getEl().setTopPx(36);
    }

    protected slideOutTop() {
        this.getEl().setTopPx(-window.outerHeight);
    }

    protected slideInBottom() {
        this.getEl().setTopPx(36);
    }

    protected slideOutBottom() {
        this.getEl().setTopPx(window.outerHeight);
    }

    notifySlidedIn() {
        this.slidedInListeners.forEach((listener: ()=> void) => listener());
    }

    onSlidedIn(listener: () => void) {
        this.slidedInListeners.push(listener);
    }
}

export class SlidablePanelBuilder {

    private slideFrom: SLIDE_FROM = SLIDE_FROM.RIGHT;

    public setSlideFrom(value: SLIDE_FROM): SlidablePanelBuilder {
        this.slideFrom = value;
        return this;
    }

    public getSlideFrom(): SLIDE_FROM {
        return this.slideFrom;
    }
}

export enum SLIDE_FROM {

    LEFT,
    RIGHT,
    BOTTOM,
    TOP,
}
