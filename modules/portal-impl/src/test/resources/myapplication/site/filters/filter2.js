var filterHelper = require('/lib/filter-helper');

exports.responseFilter = function (req, res) {

    res.headers['X-Custom-Header2'] = 'value2';

    filterHelper.addPageContribution(res, 'headEnd', "<link rel='stylesheet' href='css/global-styles.css' type='text/css'/>");

    return res;
};
