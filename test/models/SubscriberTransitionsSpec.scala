package models

import org.scalatest.{FunSpec, Matchers}

class SubscriberTransitionsSpec extends FunSpec with Matchers {
  describe("The subscriber transitions") {
    describe ("when a user is unsubscribed") {
      val subscriber = Subscriber(1, "555-555-5555", None, SubscriberTransitions.Unsubscribed.stateName)
      val subscriberTransitions = new SubscriberTransitions(subscriber)

      it ("should return the basic help message if an unknown message is passed") {
        val (message, newSubscriber) = subscriberTransitions.transition("Hi")
        message shouldEqual "subscribe_help_msg"
        newSubscriber shouldBe None
      }

      it ("should transition the subscriber to selecting language if join is sent") {
        val (message, newSubscriber) = subscriberTransitions.transition("join")
        message shouldEqual "language_selection_msg"
        newSubscriber.get.state shouldEqual SubscriberTransitions.SelectingLanguage.stateName
      }

      it ("should transition the subscriber to selecting language if a non-English join is sent") {
        val (message, newSubscriber) = subscriberTransitions.transition("등록")
        message shouldEqual "language_selection_msg"
        newSubscriber.get.state shouldEqual SubscriberTransitions.SelectingLanguage.stateName
      }

      it ("should transition the subscriber to selecting language if join with surrounding whitespace is sent") {
        val (message, newSubscriber) = subscriberTransitions.transition(" \t\tjoin \n  \n\t\n  ")
        message shouldEqual "language_selection_msg"
        newSubscriber.get.state shouldEqual SubscriberTransitions.SelectingLanguage.stateName
      }
    }

    describe("when a user is choosing a language") {
      val subscriber = Subscriber(1, "555-555-5555", None, SubscriberTransitions.SelectingLanguage.stateName)
      val subscriberTransitions = new SubscriberTransitions(subscriber)

      it("should return a help message if an unknown language is selected") {
        val (message, newSubscriber) = subscriberTransitions.transition("Hi")
        message shouldEqual "unsupported_lang_msg"
        newSubscriber shouldBe None
      }

      it("should transition the subscriber to completed if a language is selected") {
        val (message, newSubscriber) = subscriberTransitions.transition("1")
        message shouldEqual "confirmation_msg"
        newSubscriber.get.state shouldEqual SubscriberTransitions.Complete.stateName
        newSubscriber.get.language shouldEqual Some("eng")
      }

      it("should transition the subscriber to the appropriate language") {
        val (_, newSubscriber) = subscriberTransitions.transition("3")
        newSubscriber.get.language shouldEqual Some("kor")
      }

      it("should transition the subscriber to completed if a language with surrounding whitespace is selected") {
        val (message, newSubscriber) = subscriberTransitions.transition(" \n\n1  \t\n ")
        message shouldEqual "confirmation_msg"
        newSubscriber.get.state shouldEqual SubscriberTransitions.Complete.stateName
        newSubscriber.get.language shouldEqual Some("eng")
      }
    }

    describe("when a user has completed the subscription process") {
      val subscriber = Subscriber(1, "555-555-5555", None, SubscriberTransitions.Complete.stateName)
      val subscriberTransitions = new SubscriberTransitions(subscriber)

      it ("should return information on how to unsubscribe or change languages if an unknown message is sent while subscribed") {
        val (message, newSubscriber) = subscriberTransitions.transition("Hi")
        message shouldEqual "error_msg"
        newSubscriber shouldBe None
      }

      it ("should return to the select language state if the appropriate message is sent") {
        val (message, newSubscriber) = subscriberTransitions.transition("change language")
        message shouldEqual "language_selection_msg"
        newSubscriber.get.state shouldEqual SubscriberTransitions.SelectingLanguage.stateName
      }

      it ("should return to the select language state if the appropriate non-english message is sent") {
        val (message, newSubscriber) = subscriberTransitions.transition("改變語言")
        message shouldEqual "language_selection_msg"
        newSubscriber.get.state shouldEqual SubscriberTransitions.SelectingLanguage.stateName
      }

      it ("should return to the select language state if the appropriate message with surrounding whitespace is sent") {
        val (message, newSubscriber) = subscriberTransitions.transition("      \t change language   \n")
        message shouldEqual "language_selection_msg"
        newSubscriber.get.state shouldEqual SubscriberTransitions.SelectingLanguage.stateName
      }

      it ("should unsubscribe the user if the appropriate message is sent") {
        val (message, newSubscriber) = subscriberTransitions.transition("leave")
        message shouldEqual "unsubscribed_msg"
        newSubscriber.get.state shouldEqual SubscriberTransitions.Unsubscribed.stateName
      }

      it ("should unsubscribe the user if the appropriate message with surrounding whitespace is sent") {
        val (message, newSubscriber) = subscriberTransitions.transition("  \n  leave \t")
        message shouldEqual "unsubscribed_msg"
        newSubscriber.get.state shouldEqual SubscriberTransitions.Unsubscribed.stateName
      }

      it ("should stay in the complete state if a report is sent") {
        val (message, newSubscriber) = subscriberTransitions.transition("report 1111 My Address Lane")
        message shouldEqual "report_msg"
        newSubscriber shouldBe None
      }

      it ("should stay in the complete state if a report with surrounding whitespace is sent") {
        val (message, newSubscriber) = subscriberTransitions.transition("   \t\n report 1111 My Address Lane\n\t\n   ")
        message shouldEqual "report_msg"
        newSubscriber shouldBe None
      }

      it ("should return already subscribed if the user attempts to resubscribe") {
        val (message, newSubscriber) = subscriberTransitions.transition("join")
        message shouldEqual "already_subscribed_msg"
        newSubscriber shouldBe None
      }
    }
  }

  describe("The subscriber actions") {
    describe ("when a user is unsubscribed") {
      val subscriber = Subscriber(1, "555-555-5555", None, SubscriberTransitions.Unsubscribed.stateName)
      val subscriberTransitions = new SubscriberTransitions(subscriber)

      it("should return None") {
        val action = subscriberTransitions.action("Hi")
        action shouldBe None
      }
    }

    describe("when a user is choosing a language") {
      val subscriber = Subscriber(1, "555-555-5555", None, SubscriberTransitions.SelectingLanguage.stateName)
      val subscriberTransitions = new SubscriberTransitions(subscriber)

      it("should return None") {
        val action = subscriberTransitions.action("Hi")
        action shouldBe None
      }
    }

    describe("when a user has completed the subscription process") {
      val subscriber = Subscriber(1, "555-555-5555", None, SubscriberTransitions.Complete.stateName)
      val subscriberTransitions = new SubscriberTransitions(subscriber)

      it ("should return None if a report is not sent") {
        val action = subscriberTransitions.action("Hi")
        action shouldBe None
      }

      it ("should return the alert action if a report is sent") {
        val action = subscriberTransitions.action("report 1111 My Address Lane")
        action shouldBe 'isDefined
        action.get shouldEqual SubscriberTransitions.AlertAction("1111 My Address Lane")
      }
    }
  }
}
