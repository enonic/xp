import '../../api.ts';
import {SaveSortedContentAction} from './action/SaveSortedContentAction';
import {SortContentTreeGrid} from './SortContentTreeGrid';
import {SortContentTabMenu} from './SortContentTabMenu';
import {ContentGridDragHandler} from './ContentGridDragHandler';
import {OpenSortDialogEvent} from './OpenSortDialogEvent';

import TreeNode = api.ui.treegrid.TreeNode;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import Element = api.dom.Element;
import ContentSummary = api.content.ContentSummary;
import ChildOrder = api.content.order.ChildOrder;
import TabMenuItemBuilder = api.ui.tab.TabMenuItemBuilder;
import DialogButton = api.ui.dialog.DialogButton;
import i18n = api.util.i18n;

export class SortContentDialog extends api.ui.dialog.ModalDialog {

    private sortAction: SaveSortedContentAction;

    private parentContent: api.content.ContentSummaryAndCompareStatus;

    private contentGrid: SortContentTreeGrid;

    private sortContentMenu: SortContentTabMenu;

    private curChildOrder: ChildOrder;

    private prevChildOrder: ChildOrder;

    private gridDragHandler: ContentGridDragHandler;

    private isOpen: boolean;

    private saveButton: DialogButton;

    constructor() {
        super(<api.ui.dialog.ModalDialogConfig>{
            title: i18n('dialog.sort')
        });

        this.initTabMenu();

        this.initSortContentMenu();

        this.getEl().addClass('sort-content-dialog');

        this.initSortContentGrid();

        this.initGridDragHandler();

        this.populateContentPanel();

        this.initSaveButtonWithAction();

        OpenSortDialogEvent.on((event) => {
            this.handleOpenSortDialogEvent(event);
        });

        this.addCancelButtonToBottom();
    }

    open() {
        if (!this.isOpen) {
            this.contentGrid.getGrid().resizeCanvas();
            super.open();
            this.isOpen = true;
        }
    }

    show() {
        api.dom.Body.get().appendChild(this);
        super.show();
        this.sortContentMenu.focus();
    }

    close() {
        this.remove();
        super.close();
        this.isOpen = false;
        this.contentGrid.setChildOrder(null);
        this.gridDragHandler.clearContentMovements();
    }

    getContent(): ContentSummaryAndCompareStatus {
        return this.parentContent;
    }

    private initSortContentGrid() {
        this.contentGrid = new SortContentTreeGrid();
        this.contentGrid.getEl().addClass('sort-content-grid');
        this.contentGrid.onLoaded(() => {
            this.contentGrid.render(true);
            this.centerMyself();
            if (this.contentGrid.getContentId()) {
                this.open();
            }
        });
    }

    private initGridDragHandler() {
        this.gridDragHandler = new ContentGridDragHandler(this.contentGrid);
        this.gridDragHandler.onPositionChanged(() => {
            this.sortContentMenu.selectManualSortingItem();
        });
    }

    private initTabMenu() {
        let menu = new api.ui.tab.TabMenu();
        let tabMenuItem = (<TabMenuItemBuilder>new TabMenuItemBuilder().setLabel(i18n('field.sortType'))).build();
        tabMenuItem.setActive(true);
        menu.addNavigationItem(tabMenuItem);
        menu.selectNavigationItem(0);
        menu.show();
    }

    private initSortContentMenu() {
        this.sortContentMenu = new SortContentTabMenu();
        this.sortContentMenu.show();
        this.appendChildToHeader(this.sortContentMenu);

        this.sortContentMenu.onSortOrderChanged(() => {
            this.handleOnSortOrderChangedEvent();
            this.saveButton.giveFocus();
        });
    }

    private initSaveButtonWithAction() {
        this.sortAction = new SaveSortedContentAction(this);

        this.saveButton = this.addAction(this.sortAction);
        this.saveButton.addClass('save-button');

        this.sortAction.onExecuted(() => {
            this.handleSortAction();
        });
    }

    private populateContentPanel() {
        let header = new api.dom.H6El();
        header.setHtml(i18n('dialog.sort.preface'));
        this.appendChildToContentPanel(header);
        this.appendChildToContentPanel(this.contentGrid);
    }

    private handleSortAction() {
        if (this.curChildOrder.equals(this.getParentChildOrder()) && !this.curChildOrder.isManual()) {
            this.close();
        } else {
            this.showLoadingSpinner();

            if (this.curChildOrder.isManual()) {
                this.setManualReorder(this.hasChangedPrevChildOrder() ? this.prevChildOrder : null,
                    this.gridDragHandler.getContentMovements()).done(() => this.onAfterSetOrder());
            } else {
                this.setContentChildOrder(this.curChildOrder).done(() => this.onAfterSetOrder());
            }
        }
    }

    private handleOpenSortDialogEvent(event: OpenSortDialogEvent) {
        this.parentContent = event.getContent();
        this.curChildOrder = this.getParentChildOrder();
        this.prevChildOrder = null;
        this.sortContentMenu.selectNavigationItemByOrder(this.curChildOrder);

        this.contentGrid.reload(this.parentContent);
        if (!this.parentContent.hasChildren()) {
            this.contentGrid.getEl().setAttribute('data-content', event.getContent().getPath().toString());
            this.contentGrid.addClass('no-content');
        } else {
            this.contentGrid.removeClass('no-content');
            this.contentGrid.getEl().removeAttribute('data-content');
        }
    }

    private handleOnSortOrderChangedEvent() {
        let newOrder = this.sortContentMenu.getSelectedNavigationItem().getChildOrder();
        if (!this.curChildOrder.equals(newOrder)) {
            if (!newOrder.isManual()) {
                this.curChildOrder = newOrder;
                this.contentGrid.setChildOrder(this.curChildOrder);
                /*api.content.resource.ContentSummaryAndCompareStatusFetcher.fetch(this.parentContent.getContentId()).
                 done((response: api.content.ContentSummaryAndCompareStatus) => {
                 this.contentGrid.reload(response);
                 });*/
                this.contentGrid.reload(this.parentContent);
                this.gridDragHandler.clearContentMovements();
            } else {
                this.prevChildOrder = this.curChildOrder;
                this.curChildOrder = newOrder;
                this.contentGrid.setChildOrder(this.curChildOrder);
            }
        }
    }

    private onAfterSetOrder() {
        this.hideLoadingSpinner();
        this.close();
    }

    private hasChangedPrevChildOrder(): boolean {
        return this.prevChildOrder && !this.prevChildOrder.equals(this.getParentChildOrder());
    }

    private showLoadingSpinner() {
        this.saveButton.addClass('spinner');
    }

    private hideLoadingSpinner() {
        this.saveButton.removeClass('spinner');
    }

    private setContentChildOrder(order: ChildOrder, silent: boolean = false): wemQ.Promise<api.content.Content> {
        return new api.content.resource.OrderContentRequest().setSilent(silent).setContentId(
            this.parentContent.getContentId()).setChildOrder(
            order).sendAndParse();
    }

    private setManualReorder(order: ChildOrder, movements: api.content.order.OrderChildMovements,
                             silent: boolean = false): wemQ.Promise<api.content.Content> {
        return new api.content.resource.OrderChildContentRequest().setSilent(silent).setManualOrder(true).setContentId(
            this.parentContent.getContentId()).setChildOrder(order).setContentMovements(movements).sendAndParse();
    }

    private getParentChildOrder(): ChildOrder {
        if (this.parentContent && this.parentContent.getContentSummary()) {
            return this.parentContent.getContentSummary().getChildOrder();
        }

        return null;
    }
}
