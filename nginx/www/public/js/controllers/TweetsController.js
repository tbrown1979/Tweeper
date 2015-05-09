angular.module('TweetCtrl', [])
  .controller('TweetController', ['$scope', '$http', 'wordCount', function($scope, $http, WordCount) {
    $scope.tweets = [];
    $scope.search = "";

    $scope.maxSize = 10;
    $scope.currentPage = 1;
    $scope.itemsPerPage = 10;
    $scope.wordCloud;

    $scope.wordCount = WordCount.wordCount();
    $scope.wordCloud = wordCloud();
    updateWordCloud();
    $scope.source;

    $http.post(config.hostName + '/search',{
      size: 500,
      from: 0,
      searchTerms: ["*"]
    }).success(function(data) {
        $scope.tweets = data;
        $scope.wordCount.insert(data);
        updateWordCloud();
        setTimeout(updateWordCloud(), 10000)
        //$scope.$apply();
        $scope.source = new EventSource(config.hostName + "/stream/filter/?lang=en");
        $scope.source.onmessage = function(event) {
          var tweet = JSON.parse(event.data);
          addTweet(tweet);
          updatePagination();
          $scope.wordCount.insert([tweet]);
          $scope.$apply();
          if (tweet.extended_entities) {
            console.log(tweet.extended_entities.media[0].media_url_https);
          }
        };
      });

    function updateWordCloud() {
      var length = $scope.wordCount.length();
      console.log(length);
      var words = $scope.wordCount.getWords();
      var cloudContent =
        words.map(function(d) {
          return {text: d.key, size: 10 + d.value};
        })
      $scope.wordCloud.update(cloudContent);
    }

    function addTweet(json) {
      $scope.tweets.unshift(json);
      if ($scope.tweets.length < 500) return;
      var popped = $scope.tweets.pop();
    }

    function updatePagination() {
      var begin = (($scope.currentPage - 1) * $scope.itemsPerPage);
      var end = begin + $scope.itemsPerPage;
      $scope.filteredTweets = $scope.tweets.slice(begin, end);
    }

    function createSource(url, onmsg) {
      if (typeof source !== "null") source.close();
      source = new EventSource(url);
      source.onmessage = onmsg;
    }

    $scope.numPages = function() {
      return Math.ceil($scope.tweets.length / $scope.numPerPage);
    }

    $scope.pageChanged = function() {
      var begin = (($scope.currentPage - 1) * $scope.itemsPerPage);
      var end = begin + $scope.itemsPerPage;
      $scope.filteredTweets = $scope.tweets.slice(begin, end);
    }

    $scope.searchQuery = function() {
      $scope.search = $.trim($scope.search);
      if ($scope.search === "") {
        return;
      }
      $scope.tweets = [];
      //$scope.$apply();

      createSource(
        config.hostName + "/stream/filter/?lang=en&terms=" + $scope.search.replace(" ", "+"),
        function() {
          var json = JSON.parse(event.data);
          addTweet(json);
          updatePagination();
          $scope.$apply();
        }
      );
      //console.log($scope.words);
      console.log("Search: " + $scope.search);
      var searchTerms = $scope.search.split(" ")
      $http
        .post(config.hostName + '/search',
              {
                size: (500 - $scope.tweets.length),
                from: 0,
                searchTerms: searchTerms
              })
        .success(function(data) {
          $scope.tweets.concat(data);
        });
    }


    $http.get(config.hostName + "/top/emojis").success(
      function(data) {
        console.log("DATA: " + JSON.stringify(data));
        $scope.emojis = data
      }
    )

    $http.get(config.hostName + "/top/hashtags").success(
      function(data) {
        $scope.hashtags = data
      }
    )
  }]);
