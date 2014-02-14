module app.contextwindow {

    export class BaseInspectionPanel extends api.ui.Panel {

        private nameAndIcon: api.app.NamesAndIconView;
        private iconClass: string;

        constructor(iconClass: string) {
            super("inspection-panel");

            this.iconClass = iconClass;
            this.nameAndIcon =
            new api.app.NamesAndIconView(new api.app.NamesAndIconViewBuilder().
                setSize(api.app.NamesAndIconViewSize.medium)).
                setIconClass(this.iconClass);

            this.appendChild(this.nameAndIcon);
        }

        setName(name: string, path: string) {
            this.nameAndIcon.setMainName(name).setSubName(path);
        }

    }
}