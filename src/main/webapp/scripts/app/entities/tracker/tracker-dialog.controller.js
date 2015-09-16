'use strict';

angular.module('infinitetorrentApp').controller('TrackerDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'Tracker',
        function($scope, $stateParams, $modalInstance, entity, Tracker) {

        $scope.tracker = entity;
        $scope.load = function(id) {
            Tracker.get({id : id}, function(result) {
                $scope.tracker = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('infinitetorrentApp:trackerUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.tracker.id != null) {
                Tracker.update($scope.tracker, onSaveFinished);
            } else {
                Tracker.save($scope.tracker, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
