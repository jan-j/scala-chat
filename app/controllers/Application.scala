package controllers

import play.api._
import play.api.libs.EventSource
import play.api.libs.iteratee.Concurrent
import play.api.libs.json.JsValue
import play.api.mvc._

class Application extends Controller {

  val (chatOut, chatChannel) = Concurrent.broadcast[JsValue]

  def index = Action {
    Ok(views.html.index(routes.Application.chatFeed(), routes.Application.postMessage()))
  }

  def chatFeed = Action { req =>
    println("User connected to chat: " + req.remoteAddress)
    Ok.chunked(chatOut
      &> EventSource()
    ).as("text/event-stream")
  }

  def postMessage = Action(parse.json) { req =>
    chatChannel.push(req.body)
    Ok
  }

}
