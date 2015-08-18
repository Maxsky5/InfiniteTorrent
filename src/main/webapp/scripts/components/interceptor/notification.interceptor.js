 'use strict';

angular.module('infinitetorrentApp')
    .factory('notificationInterceptor', function ($q, AlertService) {
        return {
            response: function(response) {
                var alertKey = response.headers('X-infinitetorrentApp-alert');
                if (angular.isString(alertKey)) {
                    AlertService.success(alertKey, { param : response.headers('X-infinitetorrentApp-params')});
                }
                return response;
            },
        };
    });