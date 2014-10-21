angular.module('filters', [])
  .filter("fromNow", function () {
    return function (date) {
      var timeString = moment(date).fromNow(true);
      if (timeString === "a few seconds") { return "just now"; }
      else { return timeString; }
    }
  }).filter("numberFormat", function () {
    return function (number) {
      if (number < 1000) { return number; }
      else if (number < 100000) { return (Math.round(number / 100) / 10) + "K"}
      else if (number < 1000000) { return Math.round(number / 1000) + "K"}
      else { return (Math.round(number / 100000) / 10) + "M"}
    }
  }).filter('linkifyTweet', function ($sce) {
    return function (str) {
      var replaceMentions = function(tweetStr) {
        var mentionList = str.match(/@([^!"&()@*+,-\.\/;:<=>?\[\\\]^`“”\{|\}~\s]+)/g);
        if (!mentionList) return tweetStr;
        mentionList.forEach(function(mention) {
          var mentionLink = "https://twitter.com/" + mention;
          var tweetStrReplace = tweetStr.replace(mention, '<a href=' + mentionLink + ">" + mention + "</a>");
          tweetStr = tweetStrReplace;
        })
        return tweetStr;
      };
      var createAnchors = function(str) {
        return str.
          replace(/</g, '&lt;').
          replace(/>/g, '&gt;').
          replace(/(http[^\s]+)/g, '<a href="$1">$1</a>');
      };
      var toReturn = replaceMentions(createAnchors(str));
      return $sce.trustAsHtml(toReturn);
    }
  });
