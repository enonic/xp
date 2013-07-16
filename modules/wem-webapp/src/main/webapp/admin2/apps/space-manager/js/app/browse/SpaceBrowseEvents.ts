module app_browse {

    export class BaseSpaceModelEvent extends api_event.Event {
        private model:api_model.SpaceModel[];

        constructor(name:string, model:api_model.SpaceModel[]) {
            this.model = model;
            super(name);
        }

        getModels():api_model.SpaceModel[] {
            return this.model;
        }
    }

    export class GridSelectionChangeEvent extends BaseSpaceModelEvent {

        constructor(model:api_model.SpaceModel[]) {
            super('gridChange', model);
        }

        static on(handler:(event:GridSelectionChangeEvent) => void) {
            api_event.onEvent('gridChange', handler);
        }
    }

    export class GridDeselectEvent extends BaseSpaceModelEvent {

        constructor(model:api_model.SpaceModel[]) {
            super('removeFromGrid', model);
        }

        static on(handler:(event:GridDeselectEvent) => void) {
            api_event.onEvent('removeFromGrid', handler);
        }
    }

    export class NewSpaceEvent extends api_event.Event {

        constructor() {
            super('newSpace');
        }

        static on(handler:(event:NewSpaceEvent) => void) {
            api_event.onEvent('newSpace', handler);
        }
    }

    export class EditSpaceEvent extends BaseSpaceModelEvent {

        constructor(model:api_model.SpaceModel[]) {
            super('editSpaceEvent', model);
        }

        static on(handler:(event:EditSpaceEvent) => void) {
            api_event.onEvent('editSpaceEvent', handler);
        }
    }

    export class OpenSpaceEvent extends BaseSpaceModelEvent {

        constructor(model:api_model.SpaceModel[]) {
            super('openSpace', model);
        }

        static on(handler:(event:OpenSpaceEvent) => void) {
            api_event.onEvent('openSpace', handler);
        }
    }

    export class SpaceDeletePromptEvent extends BaseSpaceModelEvent {

        constructor(model:api_model.SpaceModel[]) {
            super('deletePrompt', model);
        }

        static on(handler:(event:app_browse.SpaceDeletePromptEvent) => void) {
            api_event.onEvent('deletePrompt', handler);
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

    export class CloseSpaceEvent extends api_event.Event {

        private panel:api_ui.Panel;

        private checkCanRemovePanel:bool;

        constructor(panel:api_ui.Panel, checkCanRemovePanel?:bool = true) {
            super('closeSpaceEvent');
            this.panel = panel;
            this.checkCanRemovePanel = checkCanRemovePanel;
        }

        getPanel():api_ui.Panel {
            return this.panel;
        }

        isCheckCanRemovePanel() {
            return this.checkCanRemovePanel;
        }

        static on(handler:(event:CloseSpaceEvent) => void) {
            api_event.onEvent('closeSpaceEvent', handler);
        }
    }

}
