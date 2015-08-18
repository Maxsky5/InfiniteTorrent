'use strict';

angular.module('infinitetorrentApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


