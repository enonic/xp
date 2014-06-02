module api.liveedit {

    import Content = api.content.Content;

    export class ContentView extends ItemView {

        constructor(element?: HTMLElement) {
            super(ContentItemType.get(), element);
        }

        getName(): string {

            return "[No name]";
        }

        select() {

            super.select();
        }
    }
}