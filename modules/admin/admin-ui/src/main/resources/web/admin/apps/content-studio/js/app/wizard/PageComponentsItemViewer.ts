module app.wizard {

    import ItemView = api.liveedit.ItemView;
    import ItemType = api.liveedit.ItemType;
    import PageView = api.liveedit.PageView;
    import PageItemType = api.liveedit.PageItemType;
    import Content = api.content.Content;
    import TextItemType = api.liveedit.text.TextItemType;
    import FragmentItemType = api.liveedit.fragment.FragmentItemType;
    import TextComponentView = api.liveedit.text.TextComponentView;
    import FragmentComponentView = api.liveedit.fragment.FragmentComponentView;
    import TextComponent = api.content.page.region.TextComponent;

    export class PageComponentsItemViewer extends api.ui.NamesAndIconViewer<ItemView> {

        private content: Content;

        constructor(content: Content) {
            this.content = content;
            super('page-components-item-viewer');
        }

        resolveDisplayName(object: ItemView): string {
            if (api.ObjectHelper.iFrameSafeInstanceOf(object.getType(), TextItemType)) {
                return this.extractTextFromTextComponentView(object);

            } else if (api.ObjectHelper.iFrameSafeInstanceOf(object.getType(), FragmentItemType)) {
                let fragmentView = <FragmentComponentView> object;
                let fragmentComponent = fragmentView.getFragmentRootComponent();
                if (fragmentComponent)
                {
                    if (api.ObjectHelper.iFrameSafeInstanceOf(fragmentComponent, TextComponent)) {
                        return this.extractTextFromTextComponent(<TextComponent>fragmentComponent);
                    }
                    return fragmentComponent.getName().toString();
                }
            }

            return object.getName();
        }

        resolveSubName(object: ItemView, relativePath: boolean = false): string {
            if (api.ObjectHelper.iFrameSafeInstanceOf(object.getType(), FragmentItemType)) {
                let fragmentView = <FragmentComponentView> object;
                let fragmentComponent = fragmentView.getFragmentRootComponent();
                if (fragmentComponent) {
                    return fragmentComponent.getType().getShortName();
                }
            }

            return object.getType() ? object.getType().getShortName() : "";
        }

        resolveIconUrl(object: ItemView): string {
            if (PageItemType.get().equals(object.getType())) {
                return object.getIconUrl(this.content);
            }
            return null;
        }

        resolveIconClass(object: ItemView): string {
            return object.getIconClass();
        }

        private extractTextFromTextComponentView(object: ItemView): string {
            return wemjq(object.getHTMLElement()).text().trim();
        }

        private extractTextFromTextComponent(textComponent: TextComponent): string {
            var tmp = document.createElement("DIV");
            tmp.innerHTML = textComponent.getText();
            return (tmp.textContent || tmp.innerText || "").trim();
        }
    }

}