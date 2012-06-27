AdminLiveEdit.Button = (function () {
    function createButton(config) {
        var text = config.text || '';
        var id = config.id || '';
        var iconCls = config.iconCls || '';

        var html = '<div id="' + id + '" class="live-edit-button"><a class="live-edit-button-inner">';
        if (iconCls !== '') {
            html += '<span class="live-edit-button-icon ' + iconCls + '"></span>';
        }
        html += '<span>' + text + '</span></a></div>';

        var button = $liveedit(html);

        if (config.handler) {
            button.on('click', function () {
                config.handler.call(this);
            });
        }
        return button;
    }


    // *****************************************************************************************************************
    // Public

    return {
        create: function (config) {
            return createButton(config);
        }
    };

}());