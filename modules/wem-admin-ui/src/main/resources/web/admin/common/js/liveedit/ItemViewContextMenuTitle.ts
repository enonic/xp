module api.liveedit {

    export class ItemViewContextMenuTitle extends api.app.NamesAndIconView {

        constructor(name: string, icon: string) {
            super(new api.app.NamesAndIconViewBuilder().setAddTitleAttribute(false));
            this.setMainName(name);
            this.setIconClass(icon);
        }

    }

}