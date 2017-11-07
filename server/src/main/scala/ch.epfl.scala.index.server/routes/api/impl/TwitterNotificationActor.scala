package ch.epfl.scala.index
package server
package routes
package api
package impl

import com.danielasfregola.twitter4s.TwitterRestClient
import com.danielasfregola.twitter4s.entities.{AccessToken, ConsumerToken}
import akka.actor.{Actor, ActorSystem}
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

class TwitterNotificationActor(
                              implicit val system: ActorSystem,
                              implicit val materializer: ActorMaterializer
) extends Actor {

  private val config =
    ConfigFactory.load().getConfig("org.scala_lang.index.server.data")

  private val consumer =
    if (config.hasPath("twitter.consumer"))
      ConsumerToken(config.getString("twitter.consumer.key"), config.getString("twitter.consumer.secret")  )
    else
      sys.error("Twitter Consumer Key and Secret must be provided.")

  private val access =
    if (config.hasPath("twitter.access"))
      AccessToken(config.getString("twitter.access.key"), config.getString("twitter.access.secret") )
    else
      sys.error("Twitter Access Key and Secret must be provided.")

  private val twitterClient =  TwitterRestClient(consumer, access)

  def receive = {
    case notif : TwitterNotification =>
         twitterClient.createTweet(
           s"${notif.organization} just released ${notif.name} ${notif.version} for Scala ${notif.scalaVersion}."
         )
  }

}

case class TwitterNotification (
     organization: String,
     name: String,
     version: String,
     scalaVersion: String
)