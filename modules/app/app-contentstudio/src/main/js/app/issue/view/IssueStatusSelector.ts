import {IssueStatus, IssueStatusFormatter} from '../IssueStatus';
import TabMenuItemBuilder = api.ui.tab.TabMenuItemBuilder;
import TabMenuItem = api.ui.tab.TabMenuItem;
import TabMenu = api.ui.tab.TabMenu;


export class IssueStatusSelector extends TabMenu {

    private static OPTIONS = [
        {value: IssueStatus.OPEN, name: 'Open'},
        {value: IssueStatus.CLOSED, name: 'Closed'}
    ];

    private value: IssueStatus;

    private valueChangedListeners: {(event: api.ValueChangedEvent): void}[] = [];

    constructor() {
        super('issue-status-selector');

        IssueStatusSelector.OPTIONS.forEach((option, index: number) => {
            const menuItem: TabMenuItem = TabMenuItem.create()
                .setLabel(option.name)
                .setAddLabelTitleAttribute(false)
                .build();

            this.addNavigationItem(menuItem);


        });

        this.onNavigationItemSelected((event: api.ui.NavigatorEvent) => {
            let item: api.ui.tab.TabMenuItem = <api.ui.tab.TabMenuItem> event.getItem();
            this.setValue(IssueStatusSelector.OPTIONS[item.getIndex()].value);
        });
    }

    getValue(): IssueStatus {
        return this.value;
    }

    setValue(value: IssueStatus, silent?: boolean): IssueStatusSelector {
        let option = this.findOptionByValue(value);
        if (option) {
            this.selectNavigationItem(IssueStatusSelector.OPTIONS.indexOf(option));

            this.removeClass(IssueStatusSelector.OPTIONS
                .map(curOption => curOption.name.toLowerCase())
                .join(' '));
            this.addClass(option.name.toLowerCase());

            if (!silent) {
                this.notifyValueChanged(
                    new api.ValueChangedEvent(IssueStatusFormatter.formatStatus(this.value), IssueStatusFormatter.formatStatus(value)));
            }
            this.value = value;
        }
        return this;
    }

    protected showMenu() {
        if (this.getSelectedNavigationItem().isVisibleInMenu()) {
            this.resetItemsVisibility();
            this.getSelectedNavigationItem().setVisibleInMenu(false);
        }

        let menu = this.getMenuEl();
        let entry = menu.getParentElement().getParentElement();
        let list = entry.getParentElement();
        let offset = entry.getEl().getOffsetTopRelativeToParent() -
                     (list.getEl().getOffsetTopRelativeToParent() + list.getEl().getPaddingTop() + list.getEl().getScrollTop());
        let height = menu.getEl().getHeightWithoutPadding();

        if (offset > height) {
            menu.addClass('upward');
        } else {
            menu.removeClass('upward');
        }

        super.showMenu();
    }

    protected setButtonLabel(value: string): IssueStatusSelector {
        this.getTabMenuButtonEl().setLabel(value, false);
        return this;
    }

    private findOptionByValue(value: IssueStatus) {
        for (let i = 0; i < IssueStatusSelector.OPTIONS.length; i++) {
            let option = IssueStatusSelector.OPTIONS[i];
            if (option.value === value) {
                return option;
            }
        }
        return undefined;
    }

    onValueChanged(listener: (event: api.ValueChangedEvent)=>void) {
        this.valueChangedListeners.push(listener);
    }

    unValueChanged(listener: (event: api.ValueChangedEvent)=>void) {
        this.valueChangedListeners = this.valueChangedListeners.filter((curr) => {
            return curr !== listener;
        });
    }

    private notifyValueChanged(event: api.ValueChangedEvent) {
        this.valueChangedListeners.forEach((listener) => {
            listener(event);
        });
    }

}