exports.view = {};

// Render Thymeleaf view
exports.view.render = function(view, params) {
    return {
        body: execute('thymeleaf.render', {
            view: view,
            model: params
        })
    };
};