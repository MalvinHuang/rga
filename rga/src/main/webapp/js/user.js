

    angular.module('user', ['ngRoute'])

    .factory('userData',function($http, $q){
	    return{
	      apiPath:'api/users/',
	      getAllItems: function(){
	        //Creating a deferred object
	        var deferred = $q.defer();
	 
	        //Calling Web API to fetch shopping cart items
	        $http.get(this.apiPath).success(function(data){
	          //Passing data to deferred's resolve function on successful completion
	          deferred.resolve(data);
	      }).error(function(){
	 
	        //Sending a friendly error message in case of failure
	        deferred.reject("An error occured while fetching items");
	      });
	 
	      //Returning the promise object
	      return deferred.promise;
	    }
	  }
	})
	
    .config(function($routeProvider) {
      $routeProvider
      	.when('/login', {
      		controller:'LoginController as login',
      		templateUrl:'login.html'
      	})
        .when('/', {
          controller:'UserListController as userList',
          templateUrl:'list.html',
          resolve: {
            users: function (userData) {
              return userData.getAllItems();
            }
          }
        })
        .when('/edit/:userId', {
          controller:'EditUserController as editUser',
          templateUrl:'detail.html',
          resolve: {
              users: function (userData) {
                return userData.getAllItems();
              }
            }
        })
        .when('/new', {
          controller:'NewUserController as editUser',
          templateUrl:'detail.html'
        })
        .otherwise({
          redirectTo:'/'
        });
    })
     
    .controller('UserListController', function(users) {
      var userList = this;
      userList.users = users;
    })
     
    .controller('NewUserController', function($http, $location, userData) {
      var editUser = this;
      editUser.save = function() {
    	  $http.get('api/users/new-id').success(function(data) {
    		  editUser.user.id = data;
    	   	  $http.put('api/users/' + editUser.user.id, editUser.user).success(function(data) {
        		  $location.path('/list.html');
        	  }).error(function(data) {
        		  alert(data);
        	  })
    	  });
       };
    })
     
    .controller('EditUserController', function($http, $location, $routeParams, users) {
        var editUser = this;
        var userId = $routeParams.userId;
        
        angular.forEach(users, function(user) {
        	if (user.id == userId) {
        		editUser.user = user;
        	}
        });
        
        editUser.destroy = function() {
        	$http.delete('api/users/' + editUser.user.id).success(function(data) {
        		$location.path('/list.html');
        	})
        };
     
        editUser.save = function() {
        	$http.post('api/users/' + editUser.user.id, editUser.user).success(function(data) {
      		  $location.path('/list.html');
      	  }).error(function(data) {
      		  alert(data);
      	  })
        };
    })
    
    .controller('LoginController', function($rootScope, $scope, $http, $location) {

    	  var authenticate = function(credentials, callback) {

    	    var headers = credentials ? {authorization : "Basic "
    	        + btoa(credentials.username + ":" + credentials.password)
    	    } : {};

    	    $http.get('api/user', {headers : headers}).success(function(data) {
    	      if (data.name) {
    	        $rootScope.authenticated = true;
    	      } else {
    	        $rootScope.authenticated = false;
    	      }
    	      callback && callback();
    	    }).error(function() {
    	      $rootScope.authenticated = false;
    	      callback && callback();
    	    });

    	  }

    	  authenticate();
    	  $scope.credentials = {};
    	  
    	  $scope.login = function() {
    	      authenticate($scope.credentials, function() {
    	        if ($rootScope.authenticated) {
    	          $location.path("/");
    	          $scope.error = false;
    	        } else {
    	          $location.path("/login");
    	          $scope.error = true;
    	        }
    	      });
    	  };
    	  
    	  $scope.logout = function() {
    		  $http.post('api/logout', {}).success(function() {
    		    $rootScope.authenticated = false;
    		    $location.path("/login");
    		  }).error(function(data) {
    		    $rootScope.authenticated = false;
    		  });
    		}
    	});

