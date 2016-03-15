module app.publish {

    import BrowseItem = api.app.browse.BrowseItem;
    import CompareStatus = api.content.CompareStatus;

    export class SelectionPublishItem<M extends ContentPublishItem> extends api.dom.DivEl {

        private viewer: api.ui.Viewer<M>;

        private item: BrowseItem<M>;

        private checkBox: api.ui.Checkbox;

        private statusDiv: api.dom.DivEl;

        private hidden: boolean;

        private isCheckboxHidden: boolean;

        private childrenSwitch: api.dom.SpanEl;

        private childrenDiv: DependantsItemList = new DependantsItemList();

        private dependants: SelectionPublishItem<ContentPublishItem>[] = [];

        private expandable: boolean;

        private removable: boolean;

        private showStatus: boolean;

        private removeEl: api.dom.DivEl;

        constructor(viewer: api.ui.Viewer<M>,
                    item: BrowseItem<M>,
                    status: CompareStatus,
                    isCheckboxHidden: boolean,
                    showStatus: boolean,
                    changeCallback?: () => void,
                    toggleCallback?: (isExpand: boolean) => void,
                    isCheckBoxEnabled: boolean = true,
                    isHidden: boolean = false,
                    isChecked: boolean = true,
                    expandable: boolean = false,
                    removable: boolean = false,
                    removeCallback?: () => void) {
            super("browse-selection-publish-item");
            this.viewer = viewer;
            this.item = item;
            this.hidden = isHidden;
            this.isCheckboxHidden = isCheckboxHidden;
            this.expandable = expandable;
            this.removable = removable;
            this.showStatus = showStatus;
            this.initStatusDiv(status);
            this.initCheckBox(isCheckBoxEnabled, isChecked, changeCallback);
            this.initChildrenSwitch(expandable, toggleCallback);
            this.initRemoveButton(removeCallback);
        }

        private initStatusDiv(status: CompareStatus) {
            if (this.showStatus) {
                this.statusDiv = new api.dom.DivEl("status");
                this.statusDiv.setHtml(api.content.CompareStatusFormatter.formatStatus(status));
                var statusClass = "" + CompareStatus[status];
                this.statusDiv.addClass(statusClass.toLowerCase());
            }
        }

        private initChildrenSwitch(expandable: boolean, toggleCallback?: (isExpand: boolean) => void) {
            if (expandable) {
                this.childrenSwitch = new api.dom.SpanEl("toggle icon expand");
                this.childrenSwitch.onClicked((event) => {
                    var isExpand = this.childrenSwitch.hasClass("expand");
                    this.childrenSwitch.toggleClass("expand");
                    this.childrenSwitch.toggleClass("collapse");
                    toggleCallback(isExpand);
                });
            }
        }

        private initCheckBox(isCheckBoxEnabled: boolean, isChecked: boolean, callback?: () => void) {
            if (!this.isCheckboxHidden) {
                this.checkBox = new api.ui.Checkbox();
                this.checkBox.addClass("checkbox")
                this.checkBox.setChecked(isChecked);
                if (!isCheckBoxEnabled) {
                    this.checkBox.setDisabled(true);
                    this.checkBox.addClass("disabled");
                } else {
                    this.checkBox.onValueChanged((event: api.ValueChangedEvent) => {
                        if (callback) {
                            callback();
                        }
                    });
                }
            }
        }

        private initRemoveButton(callback?: () => void) {
            if (this.removable) {
                this.removeEl = new api.dom.DivEl("icon remove");
                this.removeEl.onClicked((event: MouseEvent) => {
                    event.preventDefault();
                    event.stopPropagation();

                    if (callback) {
                        callback();
                    }
                });
            }
        }

        getBrowseItem(): BrowseItem<M> {
            return this.item;
        }

        isChecked(): boolean {
            return this.checkBox.isChecked();
        }

        isHidden(): boolean {
            return this.hidden;
        }

        isExpandable(): boolean {
            return this.expandable;
        }

        setChecked(value: boolean) {
            this.checkBox.setChecked(value);
        }

        updateViewer(viewer: api.ui.Viewer<M>) {
            this.viewer = viewer;
            this.render();
        }

        appendDependant(dependant: SelectionPublishItem<ContentPublishItem>) {
            this.dependants.push(dependant)
            this.childrenDiv.appendChild(dependant);
            dependant.doRender();
        }

        showDependants() {
            this.dependants.forEach((item: SelectionPublishItem<ContentPublishItem>)  => {
                item.show();
            });
            ;
        }

        hideDependants() {
            this.dependants.forEach((item: SelectionPublishItem<ContentPublishItem>)  => {
                item.hide();
            });
        }

        hasDependants(): boolean {
            return this.dependants.length > 0;
        }

        doRender(): boolean {
            this.removeChildren();
            if (this.expandable) {
                this.appendChild(this.childrenSwitch);
            }
            if (this.removable) {
                this.appendChild(this.removeEl);
            }
            if (!this.isCheckboxHidden) {
                this.appendChild(this.checkBox);
            }
            this.appendChild(this.viewer);
            if (this.showStatus) {
                this.appendChild(this.statusDiv);
            }
            if (this.expandable) {
                this.appendChild(this.childrenDiv);
            }

            return true;
        }
    }

    export class DependantsItemList extends api.dom.DivEl {
        constructor() {
            super();
            this.getEl().addClass("item-list");
            this.getEl().addClass("dependant-items");
        }

        clear() {
            this.removeChildren();
        }
    }

    export class SelectionPublishItemBuilder<M extends ContentPublishItem> {

        private viewer: api.ui.Viewer<M>;
        private browseItem: BrowseItem<M>;
        content: M;
        changeCallback: () => void;
        toggleCallback: (isExpand: boolean) => void;
        removeCallback: () => void;
        isCheckBoxEnabled: boolean = true;
        isHidden: boolean = false;
        isCheckboxHidden: boolean = false;
        isChecked: boolean = true;
        expandable: boolean = false;
        removable: boolean = false;
        showStatus: boolean = true;

        setViewer(viewer: api.ui.Viewer<M>): SelectionPublishItemBuilder<M> {
            this.viewer = viewer;
            return this;
        }

        setItem(item: BrowseItem<M>): SelectionPublishItemBuilder<M> {
            this.browseItem = item;
            return this;
        }

        setContent(content: M): SelectionPublishItemBuilder<M> {
            this.content = content;
            return this;
        }

        setChangeCallback(changeCallback?: () => void): SelectionPublishItemBuilder<M> {
            this.changeCallback = changeCallback;
            return this;
        }

        setRemoveCallback(removeCallback?: () => void): SelectionPublishItemBuilder<M> {
            this.removeCallback = removeCallback;
            return this;
        }

        setToggleCallback(toggleCallback?: (isExpand: boolean) => void): SelectionPublishItemBuilder<M> {
            this.toggleCallback = toggleCallback;
            return this;
        }

        setIsCheckBoxEnabled(isCheckBoxEnabled: boolean): SelectionPublishItemBuilder<M> {
            this.isCheckBoxEnabled = isCheckBoxEnabled;
            return this;
        }

        setIsHidden(isHidden: boolean): SelectionPublishItemBuilder<M> {
            this.isHidden = isHidden;
            return this;
        }

        setShowStatus(showStatus: boolean): SelectionPublishItemBuilder<M> {
            this.showStatus = showStatus;
            return this;
        }

        setIsCheckboxHidden(isCheckboxHidden: boolean): SelectionPublishItemBuilder<M> {
            this.isCheckboxHidden = isCheckboxHidden;
            return this;
        }

        setIsChecked(isChecked: boolean): SelectionPublishItemBuilder<M> {
            this.isChecked = isChecked;
            return this;
        }

        setExpandable(expandable: boolean): SelectionPublishItemBuilder<M> {
            this.expandable = expandable;
            return this;
        }

        setRemovable(removable: boolean): SelectionPublishItemBuilder<M> {
            this.removable = removable;
            return this;
        }

        create(): SelectionPublishItemBuilder<M> {
            return new SelectionPublishItemBuilder<M>();
        }

        build(): SelectionPublishItem<M> {
            this.viewer.setObject(this.content);

            this.browseItem = new BrowseItem<M>(this.content).
                setId(this.content.getId()).
                setDisplayName(this.content.getDisplayName()).
                setPath(this.content.getPath().toString()).
                setIconUrl(this.content.getIconUrl());

            return new SelectionPublishItem<M>(this.viewer, this.browseItem, this.content.getCompareStatus(), this.isCheckboxHidden,
                this.showStatus, this.changeCallback, this.toggleCallback, this.isCheckBoxEnabled,
                this.isHidden, this.isChecked, this.expandable, this.removable, this.removeCallback);
        }
    }

}
