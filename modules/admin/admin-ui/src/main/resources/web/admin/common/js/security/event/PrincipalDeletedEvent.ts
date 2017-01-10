module api.security.event {

    export class PrincipalDeletedEvent extends api.event.Event {

        private principalDeletedPaths: string[] = [];

        constructor() {
            super();
        }

        addItem(principalPath: string): PrincipalDeletedEvent {
            this.principalDeletedPaths.push(principalPath);
            return this;
        }

        getDeletedItems(): string[] {
            return this.principalDeletedPaths;
        }

        isEmpty(): boolean {
            return this.principalDeletedPaths.length == 0;
        }

        fire() {
            if (!this.isEmpty()) {
                super.fire();
            }
        }

        static on(handler: (event: PrincipalDeletedEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: PrincipalDeletedEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }

}