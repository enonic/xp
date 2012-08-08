AdminLiveEdit.ui.Button = (function () {
    function createButton(config) {
        var id = config.id || '';
        var text = config.text || '';
        var iconCls = config.iconCls || '';

        var $html = '<div id="' + id + '" class="live-edit-button">';
        if (iconCls !== '') {
            $html += '<span class="live-edit-button-icon ' + iconCls + '"></span>';
        }
        $html += '<span class="live-edit-button-text">' + text + '</span></div>';

        var $button = $liveedit($html);

        if (config.handler) {
            $button.on('click', function (event) {
                config.handler.call(this, event);
            });
        }
        return $button;
    }

    // ********************************************************************************************************************************** //
    // Define public methods

    return {
        create: function (config) {
            return createButton(config);
        }
    };

}());