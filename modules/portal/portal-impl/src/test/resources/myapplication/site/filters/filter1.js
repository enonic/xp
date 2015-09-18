var filterHelper = require('/lib/filter-helper');

exports.responseFilter = function (req, res) {

    res.headers['X-Custom-Header'] = 'value1';
    res.status = 202;

    filterHelper.addPageContribution(res, 'bodyEnd', "<script src='js/my-script.js' type='text/javascript'></script>");

    return res;
};
