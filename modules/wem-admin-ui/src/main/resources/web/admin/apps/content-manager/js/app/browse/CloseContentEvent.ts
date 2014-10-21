module app.browse {

    import Event = api.event.Event;
    import Panel = api.ui.panel.Panel;

    export class CloseContentEvent extends Event {

        private panel: Panel;

        private checkCanRemovePanel: boolean;

        constructor(panel: Panel, checkCanRemovePanel: boolean = true) {
            super();
            this.panel = panel;
            this.checkCanRemovePanel = checkCanRemovePanel;
        }

        getPanel(): Panel {
            return this.panel;
        }

        isCheckCanRemovePanel() {
            return this.checkCanRemovePanel;
        }

        static on(handler: (event: CloseContentEvent) => void) {
            Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: CloseContentEvent) => void) {
            Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }

}
