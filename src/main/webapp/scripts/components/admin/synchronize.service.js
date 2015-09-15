'use strict';

angular.module('infinitetorrentApp')
    .factory('SynchronizeService', function ($rootScope, $http) {
        return {
            synchronize: function () {
                return $http.get('api/synchronize').then(function (response) {
                    return response.data;
                });
            }
        };
    });
