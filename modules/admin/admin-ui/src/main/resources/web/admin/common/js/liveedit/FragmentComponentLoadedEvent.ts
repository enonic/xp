module api.liveedit {

    import Event = api.event.Event;
    import FragmentComponentView = api.liveedit.fragment.FragmentComponentView;

    export class FragmentComponentLoadedEvent extends Event {

        private fragmentComponentView: FragmentComponentView;

        constructor(fragmentComponentView: FragmentComponentView) {
            super();
            this.fragmentComponentView = fragmentComponentView;
        }

        getFragmentComponentView(): FragmentComponentView {
            return this.fragmentComponentView;
        }

        static on(handler: (event: FragmentComponentLoadedEvent) => void, contextWindow: Window = window) {
            Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: FragmentComponentLoadedEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}