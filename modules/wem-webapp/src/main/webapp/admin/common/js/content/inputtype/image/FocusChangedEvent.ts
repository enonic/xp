module api.content.inputtype.image {

    export class FocusChangedEvent {

        private focused:boolean;

        constructor(focused:boolean) {
            this.focused = focused;
        }

        isFocused():boolean {
            return this.focused;
        }
    }
}
