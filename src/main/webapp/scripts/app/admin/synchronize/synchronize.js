'use strict';

angular.module('infinitetorrentApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('synchronize', {
                parent: 'admin',
                url: '/synchronize',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'Synchronize'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/admin/synchronize/synchronize.html',
                        controller: 'SynchronizeController'
                    }
                },
                resolve: {

                }
            });
    });
