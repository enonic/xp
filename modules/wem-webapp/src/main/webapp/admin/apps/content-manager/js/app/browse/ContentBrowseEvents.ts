module app.browse {

    export class BaseContentModelEvent extends api.event.Event {

        private model:api.content.ContentSummary[];

        constructor(name:string, model:api.content.ContentSummary[]) {
            this.model = model;
            super(name);
        }

        getModels():api.content.ContentSummary[] {
            return this.model;
        }
    }

    export class GridSelectionChangeEvent extends BaseContentModelEvent {

        constructor(model:api.content.ContentSummary[]) {
            super('gridChange', model);
        }

        static on(handler:(event:GridSelectionChangeEvent) => void) {
            api.event.onEvent('gridChange', handler);
        }
    }

    export class ShowNewContentDialogEvent extends BaseContentModelEvent {

        private parentContent:api.content.ContentSummary;

        constructor(parentContent:api.content.ContentSummary) {
            super('showNewContentDialog', [parentContent]);
            this.parentContent = parentContent;
        }

        getParentContent():api.content.ContentSummary {
            return this.parentContent;
        }

        static on(handler:(event:ShowNewContentDialogEvent) => void) {
            api.event.onEvent('showNewContentDialog', handler);
        }
    }

    export class EditContentEvent extends BaseContentModelEvent {
        constructor(model:api.content.ContentSummary[]) {
            super('editContent', model);
        }

        static on(handler:(event:EditContentEvent) => void) {
            api.event.onEvent('editContent', handler);
        }
    }

    export class ViewContentEvent extends BaseContentModelEvent {

        constructor(model:api.content.ContentSummary[]) {
            super('openContent', model);
        }

        static on(handler:(event:ViewContentEvent) => void) {
            api.event.onEvent('openContent', handler);
        }
    }

    export class ShowDetailsEvent extends BaseContentModelEvent {

        constructor(model:api.content.ContentSummary[]) {
            super('showDetails', model);
        }

        static on(handler:(event:ShowDetailsEvent) => void) {
            api.event.onEvent('ShowDetails', handler);
        }

    }

    export class ShowPreviewEvent extends BaseContentModelEvent {

        constructor(model:api.content.ContentSummary[]) {
            super('showPreview', model);
        }

        static on(handler:(event:ShowPreviewEvent) => void) {
            api.event.onEvent('ShowPreview', handler);
        }

    }

    export class DuplicateContentEvent extends BaseContentModelEvent {

        constructor(model:api.content.ContentSummary[]) {
            super('duplicateContent', model);
        }

        static on(handler:(event:DuplicateContentEvent) => void) {
            api.event.onEvent('duplicateContent', handler);
        }

    }

    export class ContentDeletePromptEvent extends BaseContentModelEvent {

        constructor(model:api.content.ContentSummary[]) {
            super('deleteContent', model);
        }

        static on(handler:(event:ContentDeletePromptEvent) => void) {
            api.event.onEvent('deleteContent', handler);
        }
    }

    export class MoveContentEvent extends BaseContentModelEvent {

        constructor(model:api.content.ContentSummary[]) {
            super('moveContent', model);
        }

        static on(handler:(event:MoveContentEvent) => void) {
            api.event.onEvent('moveContent', handler);
        }

    }

    export class ShowContextMenuEvent extends api.event.Event {

        private x:number;

        private y:number;

        constructor(x:number, y:number) {
            this.x = x;
            this.y = y;
            super('showContextMenu');
        }

        getX() {
            return this.x;
        }

        getY() {
            return this.y;
        }

        static on(handler:(event:ShowContextMenuEvent) => void) {
            api.event.onEvent('showContextMenu', handler);
        }
    }

    export class CloseContentEvent extends api.event.Event {

        private panel:api.ui.Panel;

        private checkCanRemovePanel:boolean;

        constructor(panel:api.ui.Panel, checkCanRemovePanel:boolean = true) {
            super('closeContentEvent');
            this.panel = panel;
            this.checkCanRemovePanel = checkCanRemovePanel;
        }

        getPanel():api.ui.Panel {
            return this.panel;
        }

        isCheckCanRemovePanel() {
            return this.checkCanRemovePanel;
        }

        static on(handler:(event:CloseContentEvent) => void) {
            api.event.onEvent('closeContentEvent', handler);
        }
    }

    export class ShowNewContentGridEvent extends api.event.Event {

        constructor() {
            super('showNewContentGridEvent');
        }

        static on(handler:(event:ShowNewContentGridEvent) => void) {
            api.event.onEvent('showNewContentGridEvent', handler);
        }
    }

}
