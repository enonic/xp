module app_browse {

    export class BaseContentModelEvent extends api_event.Event {

        private model:api_content.ContentSummary[];

        constructor(name:string, model:api_content.ContentSummary[]) {
            this.model = model;
            super(name);
        }

        getModels():api_content.ContentSummary[] {
            return this.model;
        }
    }

    export class GridSelectionChangeEvent extends BaseContentModelEvent {

        constructor(model:api_content.ContentSummary[]) {
            super('gridChange', model);
        }

        static on(handler:(event:GridSelectionChangeEvent) => void) {
            api_event.onEvent('gridChange', handler);
        }
    }

    export class ShowNewContentDialogEvent extends BaseContentModelEvent {

        private parentContent:api_content.ContentSummary;

        constructor(parentContent:api_content.ContentSummary) {
            super('showNewContentDialog', [parentContent]);
            this.parentContent = parentContent;
        }

        getParentContent():api_content.ContentSummary {
            return this.parentContent;
        }

        static on(handler:(event:ShowNewContentDialogEvent) => void) {
            api_event.onEvent('showNewContentDialog', handler);
        }
    }

    export class EditContentEvent extends BaseContentModelEvent {
        constructor(model:api_content.ContentSummary[]) {
            super('editContent', model);
        }

        static on(handler:(event:EditContentEvent) => void) {
            api_event.onEvent('editContent', handler);
        }
    }

    export class OpenContentEvent extends BaseContentModelEvent {

        constructor(model:api_content.ContentSummary[]) {
            super('openContent', model);
        }

        static on(handler:(event:OpenContentEvent) => void) {
            api_event.onEvent('openContent', handler);
        }
    }

    export class ShowDetailsEvent extends BaseContentModelEvent {

        constructor(model:api_content.ContentSummary[]) {
            super('showDetails', model);
        }

        static on(handler:(event:ShowDetailsEvent) => void) {
            api_event.onEvent('ShowDetails', handler);
        }

    }

    export class ShowPreviewEvent extends BaseContentModelEvent {

        constructor(model:api_content.ContentSummary[]) {
            super('showPreview', model);
        }

        static on(handler:(event:ShowPreviewEvent) => void) {
            api_event.onEvent('ShowPreview', handler);
        }

    }

    export class DuplicateContentEvent extends BaseContentModelEvent {

        constructor(model:api_content.ContentSummary[]) {
            super('duplicateContent', model);
        }

        static on(handler:(event:DuplicateContentEvent) => void) {
            api_event.onEvent('duplicateContent', handler);
        }

    }

    export class ContentDeletePromptEvent extends BaseContentModelEvent {

        constructor(model:api_content.ContentSummary[]) {
            super('deleteContent', model);
        }

        static on(handler:(event:ContentDeletePromptEvent) => void) {
            api_event.onEvent('deleteContent', handler);
        }
    }

    export class MoveContentEvent extends BaseContentModelEvent {

        constructor(model:api_content.ContentSummary[]) {
            super('moveContent', model);
        }

        static on(handler:(event:MoveContentEvent) => void) {
            api_event.onEvent('moveContent', handler);
        }

    }

    export class ShowContextMenuEvent extends api_event.Event {

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
            api_event.onEvent('showContextMenu', handler);
        }
    }

    export class CloseContentEvent extends api_event.Event {

        private panel:api_ui.Panel;

        private checkCanRemovePanel:boolean;

        constructor(panel:api_ui.Panel, checkCanRemovePanel:boolean = true) {
            super('closeContentEvent');
            this.panel = panel;
            this.checkCanRemovePanel = checkCanRemovePanel;
        }

        getPanel():api_ui.Panel {
            return this.panel;
        }

        isCheckCanRemovePanel() {
            return this.checkCanRemovePanel;
        }

        static on(handler:(event:CloseContentEvent) => void) {
            api_event.onEvent('closeContentEvent', handler);
        }
    }

    export class ShowNewContentGridEvent extends api_event.Event {

        constructor() {
            super('showNewContentGridEvent');
        }

        static on(handler:(event:ShowNewContentGridEvent) => void) {
            api_event.onEvent('showNewContentGridEvent', handler);
        }
    }

}
