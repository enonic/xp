import '../../api.ts';
import {UserItemTypesTreeGrid} from './UserItemTypesTreeGrid';
import {NewPrincipalEvent} from '../browse/NewPrincipalEvent';

import i18n = api.util.i18n;
import LoadMask = api.ui.mask.LoadMask;
import ResponsiveManager = api.ui.responsive.ResponsiveManager;

export class NewPrincipalDialog extends api.ui.dialog.ModalDialog {

    protected loadMask: LoadMask;

    private grid: UserItemTypesTreeGrid;

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
        NewPrincipalEvent.on(() => this.close());
    }

    open() {
        this.grid.resetCache();
        super.open();
    }
}
