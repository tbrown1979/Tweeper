Tweeper
=======
This is a project I created to display tweets consumed from the [Twitter Streaming API](https://dev.twitter.com/streaming/overview)

![Build Status](http://162.243.186.131/job/Master%20Branch/badge/icon)

## Overview
This project uses [Spray](https://spray.io) as a backend. 

Both the sample and filter Twitter streams are consumed. 

When tweets are received from the filter stream they are indexed in elasticsearch, and delivered to the client using [server-sent events [SSE]](https://en.wikipedia.org/wiki/Server-sent_events). 

The tweets received from the sample stream are not stored (for various reasons), and they are also not delivered to the client. Metrics are used for this stream to keep track of the tweets/sec. These metrics are delivered to the client.

The clientside aspect to this displays a continuously updating list of up to 500 tweets. A wordcount is kept of every tweet that is received and is displayed using a word cloud([D3-cloud](https://github.com/jasondavies/d3-cloud)).
Metrics received are displayed using a very simple [D3.js](https://d3js.org/) graph.

This is a very basic readme. Here is a [demo](http://tweeper.tbrown.im)

##To do
This project has reached a point that I am happy with. There are some unused features of the backend API that I want to utilize on the clientside at some point, so this is a list of unfinished features I'd like to add.

*   Retweet/Follow buttons for tweets on frontend
*   Display Top Emojis/Hashtags (These are currently tracked and available via the api, just not displayed)
*   Fill Graph with past data, instead of initializing to 0. (This shouldn't be too difficult since this information is already stored in elasticsearch.)
*   Make a cleaner UI. I'm new to frontend development.
*   Clean up javascript. (It's pretty poor)
*   Use ES percolation queries to filter tweets sent to client
*   Make a word count for tweeted programming languages.
