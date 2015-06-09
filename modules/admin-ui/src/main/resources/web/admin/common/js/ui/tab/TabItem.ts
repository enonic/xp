module api.ui.tab {

    import Action = api.ui.Action;

    export class TabItem extends api.dom.LiEl implements api.ui.NavigationItem {

        private index: number;

        private label: string;

        private labelEl: api.dom.SpanEl;

        private active: boolean = false;

        private closeAction: api.ui.Action;

        private removeButton: api.ui.button.ActionButton;

        private labelChangedListeners: {(event: TabItemLabelChangedEvent):void}[] = [];

        private closedListeners: {(event: TabItemClosedEvent):void}[] = [];

        private selectedListeners: {(event: TabItemSelectedEvent):void}[] = [];

        constructor(builder: TabItemBuilder, classes?: string) {

            super("tab-item" + (!classes ? "" : " " + classes));

            this.labelEl = new api.dom.SpanEl('label');
            this.appendChild(this.labelEl);

            this.setLabel(builder.label, builder.markUnnamed, builder.addLabelTitleAttribute);

            this.markInvalid(builder.markInvalid);

            this.closeAction = builder.closeAction;

            if (builder.closeButtonEnabled) {
                this.createRemoveButton();
            }

            this.onClicked((event: MouseEvent) => {
                this.notifySelectedListeners();
            });
        }

        private createRemoveButton() {
            if (this.closeAction && !this.removeButton) {

                this.removeButton = new api.ui.button.ActionButton(this.closeAction);
                this.removeButton.getTooltip().setSide(api.ui.Tooltip.SIDE_LEFT);
                this.removeButton.onClicked((event: MouseEvent) => {
                    event.stopPropagation();
                    event.preventDefault();
                });
                this.prependChild(this.removeButton);
            }
        }

        setIndex(value: number) {
            this.index = value;
        }

        getIndex(): number {
            return this.index;
        }

        setLabel(newValue: string, markUnnamed: boolean = false, addLabelTitleAttribute: boolean = true) {
            if (this.label == newValue) {
                return;
            }

            var oldValue = this.label;
            this.label = newValue;
            this.labelEl.setHtml(newValue, true);

            if (addLabelTitleAttribute) {
                this.labelEl.getEl().setAttribute('title', newValue);
            }

            this.labelEl.toggleClass("unnamed", markUnnamed);

            this.notifyLabelChangedListeners(newValue, oldValue);
        }

        markInvalid(markInvalid: boolean = false) {
            this.toggleClass("invalid", markInvalid);
        }

        getLabel(): string {
            return this.label;
        }

        setActive(value: boolean) {
            this.active = value;
            this.toggleClass("active", value);
        }

        isActive(): boolean {
            return this.active;
        }

        getCloseAction(): api.ui.Action {
            return this.closeAction;
        }

        setCloseAction(closeAction: api.ui.Action) {
            this.closeAction = closeAction;
        }

        onLabelChanged(listener: (event: TabItemLabelChangedEvent)=>void) {
            this.labelChangedListeners.push(listener);
        }

        onSelected(listener: (event: TabItemSelectedEvent)=>void) {
            this.selectedListeners.push(listener);
        }

        onClosed(listener: (event: TabItemClosedEvent)=>void) {
            if (this.closeAction) {
                throw new Error("Failed to set 'on closed' listener. Close action is already setted.");
            } else {
                this.closedListeners.push(listener);
            }
        }

        unLabelChanged(listener: (event: TabItemLabelChangedEvent)=>void) {
            this.labelChangedListeners =
                this.labelChangedListeners.filter((currentListener: (event: TabItemLabelChangedEvent)=>void) => {
                    return listener != currentListener;
                });
        }

        unSelected(listener: (event: TabItemSelectedEvent)=>void) {
            this.selectedListeners = this.selectedListeners.filter((currentListener: (event: TabItemSelectedEvent)=>void) => {
                return listener != currentListener;
            });
        }

        unClosed(listener: (event: TabItemClosedEvent)=>void) {
            this.closedListeners = this.closedListeners.filter((currentListener: (event: TabItemClosedEvent)=>void) => {
                return listener != currentListener;
            });
        }

        private notifyLabelChangedListeners(newValue: string, oldValue: string) {
            this.labelChangedListeners.forEach((listener: (event: TabItemLabelChangedEvent)=>void) => {
                listener.call(this, new TabItemLabelChangedEvent(this, oldValue, newValue));
            });
        }

        private notifySelectedListeners() {
            this.selectedListeners.forEach((listener: (event: TabItemSelectedEvent)=>void) => {
                listener.call(this, new TabItemSelectedEvent(this));
            });
        }

        private notifyClosedListeners() {
            this.closedListeners.forEach((listener: (event: TabItemClosedEvent)=>void) => {
                listener.call(this, new TabItemClosedEvent(this));
            });
        }

    }

    export class TabItemBuilder {

        label: string;

        addLabelTitleAttribute: boolean = true;

        closeAction: api.ui.Action;

        closeButtonEnabled: boolean;

        markUnnamed: boolean;

        markInvalid: boolean;

        setLabel(label: string): TabItemBuilder {
            this.label = label;
            return this;
        }

        setCloseAction(closeAction: api.ui.Action): TabItemBuilder {
            this.closeAction = closeAction;
            return this;
        }

        setCloseButtonEnabled(enabled: boolean): TabItemBuilder {
            this.closeButtonEnabled = enabled;
            return this;
        }

        setMarkUnnamed(markUnnamed: boolean): TabItemBuilder {
            this.markUnnamed = markUnnamed;
            return this;
        }

        setMarkInvalid(markInvalid: boolean): TabItemBuilder {
            this.markInvalid = markInvalid;
            return this;
        }

        setAddLabelTitleAttribute(addLabelTitleAttribute: boolean): TabItemBuilder {
            this.addLabelTitleAttribute = addLabelTitleAttribute;
            return this;
        }

        build(): TabItem {
            return new TabItem(this);
        }

    }

}