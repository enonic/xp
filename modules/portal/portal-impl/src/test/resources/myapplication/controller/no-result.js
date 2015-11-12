exports.get = function (req) {

    var mode = req.params['mode'];

    if (mode == 'empty') {
        return {};
    } else if (mode == 'body') {
        return {
            body: 'content'
        };
    } else if (mode == 'status') {
        return {
            status: 200
        };
    }

};
