module api.ui.selector.combobox {

    export interface SelectedOptionView<T> extends api.dom.Element {

        setOption(option: api.ui.selector.Option<T>);

        getOption(): api.ui.selector.Option<T>;

        notifySelectedOptionRemoveRequested();

        onSelectedOptionRemoveRequest(listener: {(): void;});

        unSelectedOptionRemoveRequest(listener: {(): void;});
    }
}