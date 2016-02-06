// package com.tbrown.twitterStream
// import akka.http.scaladsl.Http
// import akka.http.scaladsl.server.Route
// import akka.http.scaladsl.server.Directives._
// import akka.http.scaladsl.model.StatusCodes
// import akka.stream.ActorMaterializer

// trait HttpServer extends ActorModule {
//   def routes: Route

//   def startHttpServer(): Unit = {
//     //implicit val system = 
//     implicit val materializer = ActorMaterializer()
//     implicit val ec = system.dispatcher

//     def fullRoute = routes ~ complete(StatusCodes.NotFound)
//     val bindingFuture = Http().bindAndHandle(fullRoute, "0.0.0.0", 8080)

//     println(s"Server online at http://0.0.0.0:8080")

//     sys.addShutdownHook {
//       bindingFuture
//         .flatMap(_.unbind()) // trigger unbinding from the port
//         .onComplete(_ ⇒ system.terminate()) // and shutdown when done
//     }
//   }
// }
