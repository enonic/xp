module app.wizard.page.contextwindow.inspect.region {

    import TextComponent = api.content.page.region.TextComponent;
    import TextComponentView = api.liveedit.text.TextComponentView;
    import TextComponentViewer = api.liveedit.text.TextComponentViewer;

    export class TextInspectionPanel extends BaseInspectionPanel {

        private namesAndIcon: api.app.NamesAndIconView;

        constructor() {
            super();

            this.namesAndIcon = new api.app.NamesAndIconView(new api.app.NamesAndIconViewBuilder().
                setSize(api.app.NamesAndIconViewSize.medium)).
                setIconClass(api.liveedit.ItemViewIconClassResolver.resolveByType("text"));

            this.appendChild(this.namesAndIcon);
        }

        setTextComponent(textComponentView: TextComponentView) {

            let textComponent: TextComponent = <TextComponent>textComponentView.getComponent();

            if (textComponent) {
                let viewer = <TextComponentViewer>textComponentView.getViewer();
                this.namesAndIcon.setMainName(viewer.resolveDisplayName(textComponent, textComponentView) );
                this.namesAndIcon.setSubName(viewer.resolveSubName(textComponent));
                this.namesAndIcon.setIconClass(viewer.resolveIconClass(textComponent));
            }
        }

    }
}
