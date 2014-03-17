module app.contextwindow.inspect {

    export class BaseInspectionPanel extends api.ui.Panel {

        private nameAndIcon: api.app.NamesAndIconView;
        private iconClass: string;

        constructor(iconClass: string, renderNameAndIcon: boolean = true) {
            super("inspection-panel");

            this.iconClass = iconClass;
            if (renderNameAndIcon) {
                this.nameAndIcon = new api.app.NamesAndIconView(new api.app.NamesAndIconViewBuilder().
                    setSize(api.app.NamesAndIconViewSize.medium)).
                    setIconClass(this.iconClass);

                this.appendChild(this.nameAndIcon);
            }

            this.onRendered((event) => {
                $(this.getHTMLElement()).slimScroll({
                    height: '100%'
                });
            })
        }

        setMainName(value: string) {
            if (this.nameAndIcon) {
                this.nameAndIcon.setMainName(api.util.limitString(value, 20));
            }
        }

        setSubName(value: string) {
            if (this.nameAndIcon) {
                this.nameAndIcon.setSubName(value);
            }
        }
    }
}