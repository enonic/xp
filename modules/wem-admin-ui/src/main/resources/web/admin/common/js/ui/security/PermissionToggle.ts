module api.ui.security {

    export class PermissionToggle extends api.dom.AEl {

        private static STATES: string[] = ['allow', 'deny', 'inherit'];

        private stateIndex: number;

        constructor(label: string, state: string = 'inherit') {
            super('permission-toggle ' + state);
            this.setHtml(label);

            this.stateIndex = PermissionToggle.STATES.indexOf(state);

            this.onClicked((event: MouseEvent) => {
                this.removeClass(this.getState());
                this.stateIndex = (this.stateIndex + 1) % PermissionToggle.STATES.length;
                this.addClass(this.getState());

                event.preventDefault();
                event.stopPropagation();
            });
        }

        getState(): string {
            return PermissionToggle.STATES[this.stateIndex];
        }

    }

}