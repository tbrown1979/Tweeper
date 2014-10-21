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

    function createSource(url, termList) {
      if (typeof $scope.source !== "undefined") $scope.source.close();

      console.log(url);

      $scope.source = new EventSource(url);
      $scope.source.termList = termList;
      $scope.source.onmessage = function(event) {
        var tweet = JSON.parse(event.data);
        addTweet(tweet);
        $scope.wordCount.insert([tweet]);
        updatePagination();
        $scope.$apply();
        if (tweet.extended_entities) {
          console.log(tweet.extended_entities.media[0].media_url_https);
        }
      };
    }

    $scope.searchQuery = function(from, size, termList, url, cb) {
      console.log($scope.source);
      if (typeof $scope.source !== "undefined") {
        console.log("Source's TermList: " + $scope.source.termList);
        console.log(_.isEqual(termList, $scope.source.termList));
        if (_.isEqual(termList, $scope.source.termList)) {
          console.log("SAME TERMS");
          return;
        }
        console.log("closing the source");
        $scope.source.close();
      }

      if (termList.length === 0) {
        url = "/stream/filter/?lang=en";
      } else {
        url = "/stream/filter/?lang=en&terms=" + termList.join("+");
      }
      console.log("Search Terms: " + termList);

      $http.post(config.hostName + '/search',{
        size: size,
        from: from,
        searchTerms: termList
      }).success(function(data) {
        $scope.tweets = data;
        //$scope.$apply();
        $scope.wordCount.reset();
        $scope.wordCount.insert(data);
        cb();
        createSource(config.hostName + url, termList);
      });
    }

    $scope.searchQuery(0, 500, [], "/stream/filter/?lang=en", function() {
      updateWordCloud();
      setTimeout(updateWordCloud(), 10000);
    })

    $scope.resetStream = function() {
      $scope.search = "";
      $scope.searchQuery(0, 500, [], "/stream/filter/?lang=en", function(){});
    }

    $scope.userSearch = function() {
      if ($scope.search === "") return;
      var terms = $scope.search.split(" ");
      var termsFormatted = $scope.search.replace(" ", "+");
      $scope.searchQuery(
        0, 500, terms, "/stream/filter/?lang=en&terms=", function() {})
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

    $scope.numPages = function() {
      $scope.$apply();
      return Math.ceil($scope.tweets.length / $scope.numPerPage);
    }

    $scope.pageChanged = function() {
      var begin = (($scope.currentPage - 1) * $scope.itemsPerPage);
      var end = begin + $scope.itemsPerPage;
      $scope.filteredTweets = $scope.tweets.slice(begin, end);
    }

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
  }]);
