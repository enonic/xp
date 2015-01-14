module app.home {
    import Event = api.event.Event;
    import User = api.security.User;

    export class LogInEvent extends Event {

        private user: api.security.User;

        constructor(user: User) {
            super();
            this.user = user;
        }

        getUser() {
            return this.user;
        }

        static on(handler: (event: LogInEvent) => void, contextWindow: Window = window) {
            Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: LogInEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}