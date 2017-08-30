import '../../api.ts';
import {UserItemTypesTreeGrid} from './UserItemTypesTreeGrid';
import {NewPrincipalEvent} from '../browse/NewPrincipalEvent';
import {UserTreeGridItem, UserTreeGridItemType} from '../browse/UserTreeGridItem';

import i18n = api.util.i18n;
import LoadMask = api.ui.mask.LoadMask;
import ResponsiveManager = api.ui.responsive.ResponsiveManager;

export class NewPrincipalDialog extends api.ui.dialog.ModalDialog {

    protected loadMask: LoadMask;

    private grid: UserItemTypesTreeGrid;

    private pathEl: api.dom.PEl;

    constructor() {
        super(<api.ui.dialog.ModalDialogConfig>{
            title: i18n('dialog.new')
        });

        this.addClass('new-principal-dialog');

        this.initElements();

        this.initEventHandlers();

        this.appendElementsToDialog();

        api.dom.Body.get().appendChild(this);
    }

    private initElements() {
        this.initUserItemTypesTreeGrid();
        this.initLoadMask();
    }

    private initUserItemTypesTreeGrid() {
        this.grid = new UserItemTypesTreeGrid();
    }

    private initLoadMask() {
        this.loadMask = new LoadMask(this);
    }

    private appendElementsToDialog() {
        this.getContentPanel().appendChild(this.grid);
        this.getContentPanel().getParentElement().appendChild(this.loadMask);
    }

    private initEventHandlers() {
        this.grid.onDataChanged(() => ResponsiveManager.fireResizeEvent());
        NewPrincipalEvent.on(() => this.isVisible() && this.close());
    }

    setSelection(selection: UserTreeGridItem[]): NewPrincipalDialog {
        const isUserStore = selection.length > 0 && selection[0].getType() === UserTreeGridItemType.USER_STORE;
        if (isUserStore) {
            this.grid.setUserStore(selection[0].getUserStore());
            this.setPath(selection[0].getItemDisplayName());
        }
        else {
            this.pathEl.hide();
        }
        return this;
    }

    open() {
        this.grid.reload(null, null, false);
        this.grid.getGrid().resizeCanvas();
        super.open();
    }

    close() {
        this.grid.clearUserStores();
        super.close();
    }

    private createPathEl(): api.dom.PEl {
        const pathEl = new api.dom.PEl('path');
        pathEl.getEl().setAttribute('data-desc', `${i18n('dialog.newContent.pathDescription')}:`);
        this.header.appendChild(pathEl);

        return pathEl;
    }

    private setPath(path: string) {
        if (!this.pathEl) {
            this.pathEl = this.createPathEl();
        }
        this.pathEl.setHtml(path);
        this.pathEl.show();
    }
}