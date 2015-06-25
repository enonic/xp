var portal = require('/lib/xp/portal');
var thymeleaf = require('/lib/xp/thymeleaf');

exports.get = function (req) {
    var component = portal.getComponent();

    return {
        body: thymeleaf.render( {
            view: resolve('centered.html'),
            model: {
                centerRegion: component.regions["center"]
            }
        })
    };

};
