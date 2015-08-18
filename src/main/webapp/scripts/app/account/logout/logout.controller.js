'use strict';

angular.module('infinitetorrentApp')
    .controller('LogoutController', function (Auth) {
        Auth.logout();
    });
