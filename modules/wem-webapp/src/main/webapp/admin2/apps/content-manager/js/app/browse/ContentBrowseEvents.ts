module app_browse {

    export class BaseContentModelEvent extends api_event.Event {
        private model:api_model.ContentModel[];

        constructor(name:string, model:api_model.ContentModel[]) {
            this.model = model;
            super(name);
        }

        getModels():api_model.ContentModel[] {
            return this.model;
        }
    }

    export class GridSelectionChangeEvent extends BaseContentModelEvent {
        constructor(model:api_model.ContentModel[]) {
            super('gridChange', model);
        }

        static on(handler:(event:GridSelectionChangeEvent) => void) {
            api_event.onEvent('gridChange', handler);
        }
    }

    export class NewContentEvent extends api_event.Event {
        constructor() {
            super('newContent');
        }

        static on(handler:(event:NewContentEvent) => void) {
            api_event.onEvent('newContent', handler);
        }
    }

    export class EditContentEvent extends BaseContentModelEvent {
        constructor(model:api_model.ContentModel[]) {
            super('editContent', model);
        }

        static on(handler:(event:EditContentEvent) => void) {
            api_event.onEvent('editContent', handler);
        }
    }

    export class OpenContentEvent extends BaseContentModelEvent {

        constructor(model:api_model.ContentModel[]) {
            super('openContent', model);
        }

        static on(handler:(event:OpenContentEvent) => void) {
            api_event.onEvent('openContent', handler);
        }
    }

    export class ShowDetailsEvent extends BaseContentModelEvent {

        constructor(model:api_model.ContentModel[]) {
            super('showDetails', model);
        }

        static on(handler:(event:ShowDetailsEvent) => void) {
            api_event.onEvent('ShowDetails', handler);
        }

    }

    export class ShowPreviewEvent extends BaseContentModelEvent {

        constructor(model:api_model.ContentModel[]) {
            super('showPreview', model);
        }

        static on(handler:(event:ShowPreviewEvent) => void) {
            api_event.onEvent('ShowPreview', handler);
        }

    }

    export class DuplicateContentEvent extends BaseContentModelEvent {

        constructor(model:api_model.ContentModel[]) {
            super('duplicateContent', model);
        }

        static on(handler:(event:DuplicateContentEvent) => void) {
            api_event.onEvent('duplicateContent', handler);
        }

    }

    export class ContentDeletePromptEvent extends BaseContentModelEvent {
        constructor(model:api_model.ContentModel[]) {
            super('deleteContent', model);
        }

        static on(handler:(event:ContentDeletePromptEvent) => void) {
            api_event.onEvent('deleteContent', handler);
        }
    }

    export class GridDeselectEvent extends BaseContentModelEvent {
        constructor(model:api_model.ContentModel[]) {
            super('removeFromGrid', model);
        }

        static on(handler:(event:GridDeselectEvent) => void) {
            api_event.onEvent('removeFromGrid', handler);
        }
    }

    export class MoveContentEvent extends BaseContentModelEvent {

        constructor(model:api_model.ContentModel[]) {
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

}
