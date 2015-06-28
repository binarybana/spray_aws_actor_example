package com.mlh.spraysample

import scala.concurrent.duration._

import akka.actor.{ Actor, ActorLogging, ActorSystem, Props }
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization.{ read, write }
import spray.can._
import spray.http._
import spray.http.HttpMethods._
import spray.httpx.Json4sSupport
import spray.routing._
import spray.can.server.Stats
import spray.http.StatusCodes._
import scala.concurrent.Future
import akka.actor.ActorRef

import awscala._, s3._

import akka.agent.Agent




class MyCompute extends Actor with ActorLogging {
  var myvar = 1.0
  log.info("Created new MyCompute Actor")
  import context.dispatcher // implicit Execution context for futures

  // def compute() = this.synchronized { Thread.sleep(3000); myvar += 1.0 }
  def compute() = { Thread.sleep(3000); myvar += 1.0 }

  def receive = {
    case "compute" => {
      Future(compute())
    }
    case "retrieve" => { 
      sender ! myvar 
    }
  }
}

class MyHttpService(val dataService:Agent[String]) extends HttpServiceActor {

  implicit val timeout = Timeout(1 seconds)
  import context.dispatcher
  def receive = runRoute {
    path("ping") {
      get {
        complete(dataService.get)
      }
    } ~
    path("compute") {
      get {
        complete {
          dataService.sendOff(Boot.longCompute)
          "should be going..."
        }
      }
    }
  }
}

object Boot extends App {
  implicit val system = ActorSystem("spray-sample-system")
  import system.dispatcher
  implicit val timeout = Timeout(1 seconds)
  implicit val s3 = S3()

  def longCompute(x:String):String = {
    // Get stuff from S3
    val bucket = s3.bucket("jknight-testb").get
    val s3obj = bucket.getObject("sample.txt")
    // System.nanoTime.toString + scala.io.Source.fromInputStream(s3obj.get.content).mkString
    System.nanoTime.toString + (s3.ls(bucket, "") foreach {x => println(x.get)}
  }
    
  /* Use Akka to create our Spray Service */
  val dataService = system.actorOf(Props[MyCompute], name="MyCompute")
  val glommedJSON = Agent("init val")
  val httpService = system.actorOf(Props(classOf[MyHttpService], glommedJSON), name="MyRouter")
  // val cancellable = system.scheduler.schedule(0 milliseconds, 100 milliseconds, dataService, "compute")
  //
  // def testFun:Unit = {

  val cancellable = system.scheduler.schedule(0 milliseconds, 5000 milliseconds){
    glommedJSON.sendOff(longCompute)
  }

  /* and bind to Akka's I/O interface */
  IO(Http) ? Http.Bind(httpService, system.settings.config.getString("app.interface"), system.settings.config.getInt("app.port"))

}
